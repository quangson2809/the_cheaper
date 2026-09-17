// Generates the reviewed collection. Each request has exactly one expected status.
const fs = require('node:fs');
const items = [];
function request(name, method, path, actor, expected, body, assertions = []) {
  const header = [];
  if (actor) header.push({ key: 'Authorization', value: actor === 'invalid' ? 'Bearer invalid' : `Bearer {{${actor}Token}}` });
  if (body) header.push({key: 'Content-Type', value: 'application/json'});
  items.push({name, request: {method, header, url: `{{baseUrl}}${path}`,
    ...(body ? {body: {mode: 'raw', raw: body}} : {})},
    event: [{listen: 'test', script: {type: 'text/javascript', exec: [
      `pm.test('HTTP ${expected}', () => pm.response.to.have.status(${expected}));`,
      ...assertions
    ]}}]});
}
function state(expected) { return [`pm.test('Persisted status ${expected}', () => pm.expect(pm.response.json().data.status).to.eql('${expected}'));`]; }
function detail(name, actor, id, expected) { request(name, 'GET', `/api/admin/orders/{{${id}}}`, actor, 200, null, state(expected)); }
function patch(name, actor, id, target, expected) {
  request(name, 'PATCH', `/api/admin/orders/{{${id}}}/status`, actor, expected, JSON.stringify({status: target}), expected === 200 ? state(target) : []);
}
for (const actor of ['userA','userB','admin','reader','confirm','cancel','delivery','collect','none']) {
  request(`Login ${actor}`, 'POST', '/api/auth/login', null, 200,
    JSON.stringify({email: `{{${actor}Email}}`, password: '{{password}}'}), [
      "pm.test('Access token present', () => pm.expect(pm.response.json().data.accessToken).to.be.a('string').and.not.empty);",
      `pm.collectionVariables.set('${actor}Token', pm.response.json().data.accessToken);`
    ]);
}
request('Anonymous rejected', 'GET', '/api/orders', null, 401);
request('Invalid JWT rejected', 'GET', '/api/orders', 'invalid', 401);
for (const actor of ['userA','userB']) {
  request(`Prepare cart ${actor}`, 'POST', '/api/carts/items', actor, 201, '{"variantId":{{variantId}},"quantity":2}');
  request(`Create COD order ${actor}`, 'POST', '/api/orders', actor, 201,
    '{"paymentMethodId":{{paymentMethodId}},"receiver":"Phase 5","location":"Test address","phone":"0901234567"}', [
      "pm.test('New unpaid COD order', () => { const d=pm.response.json().data; pm.expect(d.id).to.be.a('number'); pm.expect(d.status).to.eql('PENDING'); pm.expect(d.paymentMethodCode).to.eql('COD'); pm.expect(d.finalAmount).to.eql(200); pm.expect(d.items).to.have.length(1); });",
      `pm.collectionVariables.set('${actor}OrderId', String(pm.response.json().data.id));`
    ]);
}
request('A list contains only A order', 'GET', '/api/orders', 'userA', 200, null, [
  "pm.test('Only own order', () => pm.expect(pm.response.json().data.content.map(o=>String(o.id))).to.eql([pm.collectionVariables.get('userAOrderId')]));"
]);
request('A cannot read B', 'GET', '/api/orders/{{userBOrderId}}', 'userA', 404);
request('A cannot cancel B', 'POST', '/api/orders/{{userBOrderId}}/cancel', 'userA', 404);
detail('B unchanged after IDOR', 'admin', 'userBOrderId', 'PENDING');
request('Customer cannot use staff list', 'GET', '/api/admin/orders', 'userA', 403);
request('Staff read has system scope', 'GET', '/api/admin/orders', 'reader', 200, null, [
  "pm.test('Both customer orders visible', () => { const ids=pm.response.json().data.content.map(o=>String(o.id)); pm.expect(ids).to.include(pm.collectionVariables.get('userAOrderId')); pm.expect(ids).to.include(pm.collectionVariables.get('userBOrderId')); });"
]);
for (const actor of ['none','reader','cancel','delivery','collect']) patch(`${actor} cannot confirm`, actor, 'userBOrderId', 'PROCESSING', 403);
patch('Confirm cannot cancel', 'confirm', 'userBOrderId', 'CANCELED', 403);
patch('Confirm cannot ship', 'confirm', 'userBOrderId', 'SHIPPING', 403);
detail('Order unchanged after permission denials', 'admin', 'userBOrderId', 'PENDING');
request('Cancel own valid COD order', 'POST', '/api/orders/{{userAOrderId}}/cancel', 'userA', 200, null, state('CANCELED'));
request('Read after own cancellation', 'GET', '/api/orders/{{userAOrderId}}', 'userA', 200, null, state('CANCELED'));
request('Repeated cancellation fails', 'POST', '/api/orders/{{userAOrderId}}/cancel', 'userA', 400);
detail('Canceled order stays canceled', 'admin', 'userAOrderId', 'CANCELED');
patch('Confirm valid order', 'confirm', 'userBOrderId', 'PROCESSING', 200);
detail('Confirmation persisted', 'reader', 'userBOrderId', 'PROCESSING');
patch('Dispatch valid order', 'delivery', 'userBOrderId', 'SHIPPING', 200);
detail('Shipping persisted', 'reader', 'userBOrderId', 'SHIPPING');
request('Customer cannot cancel shipped order', 'POST', '/api/orders/{{userBOrderId}}/cancel', 'userB', 400);
patch('Unpaid COD cannot be delivered', 'confirm', 'userBOrderId', 'DELIVERED', 400);
detail('Unpaid order remains shipping', 'admin', 'userBOrderId', 'SHIPPING');
patch('Delivery permission cannot confirm delivery', 'delivery', 'paidShippingOrderId', 'DELIVERED', 403);
patch('Confirm paid delivery', 'confirm', 'paidShippingOrderId', 'DELIVERED', 200);
detail('Delivered state persisted', 'reader', 'paidShippingOrderId', 'DELIVERED');
patch('Admin cannot bypass terminal state', 'admin', 'paidShippingOrderId', 'PROCESSING', 400);
detail('Terminal state preserved', 'admin', 'paidShippingOrderId', 'DELIVERED');
const collection = {
  info: {name: 'The Cheaper - Phase 5 Order RBAC', schema: 'https://schema.getpostman.com/json/collection/v2.1.0/collection.json',
    description: 'Run with the isolated environment prepared by Phase5PostmanIntegrationTest. Fresh carts and a paid SHIPPING fixture are required. Each case expects one exact HTTP result; successful writes are followed by a read. COD collection, returns and failed delivery are outside the agreed existing-API scope.'},
  variable: [{key:'baseUrl',value:'http://localhost:8080'}], item: items
};
fs.writeFileSync(require('node:path').join(__dirname, 'TheCheaper-Order-RBAC.postman_collection.json'), JSON.stringify(collection, null, 2) + '\n');
