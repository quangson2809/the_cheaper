# Module-first package structure

Business code is organized by capability first, with technical subpackages inside each module.

## Modules
- account: account/profile/address and admin account management
- auth: login/register/password reset
- rbac: roles, permissions, account-role and role-permission authorization data
- catalog: products, variants, images, brands, categories, materials and options
- cart: shopping cart
- order: customer/admin order flows
- payment: payment records and payment methods
- review: product reviews
- dashboard: administrative statistics

Cross-cutting packages: security, common, infrastructure, bootstrap, config.

## Current dependency map
account <-> rbac
auth -> account + rbac + cart + security
cart -> account + catalog
order -> account + cart + catalog + payment
payment -> order
review -> account + catalog + order
dashboard -> account + order
security -> account + rbac

These dependencies document current code, not an ideal target architecture.

## Structural invariants
This refactor must not change REST routes, JSON contracts, JPA table/column mappings, transaction boundaries, authorization semantics, business validation, stock/sold behavior, order/payment behavior, or exception/status behavior.

## Known dependency debt
- account and rbac are mutually coupled through JPA account-role relationships.
- auth creates a cart during registration.
- order reads payment methods directly.
- payment records reference orders.
- review queries order history to validate purchases.
- dashboard reads account and order repositories.

Resolve these later in behavior-focused changes, not in this structural refactor.
