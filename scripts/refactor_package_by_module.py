from __future__ import annotations
import re, subprocess
from dataclasses import dataclass
from pathlib import Path

ROOT=Path(__file__).resolve().parents[1]
MAIN=ROOT/"src/main/java/com/example/the_cheaper"
TEST=ROOT/"src/test/java/com/example/the_cheaper"

def dto_module(n):
    if n in {"ApiResponse.java","PageResponse.java"}: return "common"
    rules=[
      (r"^(Login|Register|ForgotPassword|VerifyOtp|ChangePassword|AuthResponse)","auth"),
      (r"^(AdminAccount|AdminCreateAdmin|AdminUserFilter|AssignAccountRole|UserAccount|UserAddress|UserUpdateProfile)","account"),
      (r"^(AdminPermission|AdminRole|UpdateRolePermissions)","rbac"),
      (r"^(UserCart|UserAddCartItem|UserUpdateCartItem|UserMergeCart|UserPreOrder)","cart"),
      (r"^(UserOrder|UserCreateOrder|UserCheckout|AdminOrder)","order"),
      (r"^(AdminCreatePaymentMethod|AdminUpdatePaymentMethod|PaymentMethodResponse)","payment"),
      (r"^UserReview","review"),
      (r"^(AdminDashboard|MonthlyRevenue|MonthlyQuantity|OrderStatusRatio|SearchRequest)","dashboard"),
      (r"^(AdminProduct|AdminVariant|AdminBrand|AdminCategory|AdminMaterial|AdminOption|UserProduct|UserBrand|UserCategory|UserOption|UserImage|UserVariantInfo)","catalog")]
    for p,m in rules:
        if re.match(p,n): return m

def entity_module(n):
    if n in {"AccountEntity.java","AddressEntity.java"}: return "account"
    if n=="PasswordResetTokenEntity.java": return "auth"
    if n in {"Role.java","RoleEntity.java","PermissionEntity.java","AccountRoleEntity.java","RolePermissionEntity.java"}: return "rbac"
    if n in {"CartEntity.java","CartItemEntity.java"}: return "cart"
    if n in {"OrderEntity.java","OrderItemEntity.java","OrderStatus.java"}: return "order"
    if n in {"PaymentEntity.java","PaymentMethodEntity.java"}: return "payment"
    if n=="ReviewEntity.java": return "review"
    return "catalog"

def by_file(n):
    if n=="AdminUserService.java": return "account"
    for p,m in [(r"Account|Address","account"),(r"Role|Permission|Authorization","rbac"),
                (r"Cart","cart"),(r"Order","order"),(r"PaymentMethod|PaymentEntity|PaymentRepository","payment"),
                (r"Review","review"),(r"Dashboard","dashboard"),
                (r"Product|Variant|Image|Brand|Category|Material|Option","catalog")]:
        if re.search(p,n): return m

def main_module(p):
    a=p.relative_to(MAIN).parts; n=p.name
    if n=="TheCheaperApplication.java": return "root"
    if a[0]=="annotation" and n=="CurrentUser.java": return "security"
    if a[0]=="security": return "security"
    if a[0]=="config": return "bootstrap" if n=="DataSeeder.java" else "config"
    if a[0]=="exception": return "common"
    if a[0]=="external": return "infrastructure"
    if a[0]=="entity": return entity_module(n)
    if a[0]=="dto":
        m=dto_module(n)
        if m: return m
    if a[0]=="controller" and n=="AuthController.java": return "auth"
    if a[0]=="repository" and n=="PasswordResetTokenRepository.java": return "auth"
    if a[0]=="service" and n in {"AuthService.java","PasswordService.java"}: return "auth"
    if a[0] in {"repository","controller","service","mapper"}:
        m=by_file(n)
        if m: return m
    raise RuntimeError(f"unmapped main source: {p}")

def test_module(n):
    for p,m in [(r"Dashboard","dashboard"),(r"Auth|Password","auth"),(r"Order","order"),
                (r"Permission|Role|Authorization|AccountRole","rbac"),(r"CustomUserDetails","security")]:
        if re.search(p,n): return m

def new_path(p):
    if p.is_relative_to(TEST):
        a=p.relative_to(TEST).parts; n=p.name
        if n=="TheCheaperApplicationTests.java": return p
        if a[0]=="fixtures": return TEST/"testsupport/fixtures"/n
        if a[0]=="testconfig": return TEST/"testsupport/config"/n
        m=test_module(n)
        if not m: raise RuntimeError(f"unmapped test: {p}")
        return TEST/m/("integration" if a[0]=="integration" else "unit")/n

    a=p.relative_to(MAIN).parts; n=p.name; m=main_module(p)
    if m=="root": return p
    if m=="security": return MAIN/"security"/n
    if m=="config": return p
    if m=="bootstrap": return MAIN/"bootstrap"/n
    if m=="common": return MAIN/("common/exception" if a[0]=="exception" else "common/dto")/n
    if m=="infrastructure": return MAIN/("infrastructure/mail" if n=="EmailService.java" else "infrastructure/storage")/n
    layer=a[0]
    if layer=="dto":
        target=MAIN/m/"dto"/a[1]
        if len(a)>3 and a[2] in {"admin","user"}: target=target/a[2]
        return target/n
    if layer in {"controller","mapper"}:
        target=MAIN/m/layer
        if len(a)>2 and a[1] in {"admin","user"}: target=target/a[1]
        return target/n
    return MAIN/m/layer/n

def fqcn(p):
    base=ROOT/("src/main/java" if p.is_relative_to(ROOT/"src/main/java") else "src/test/java")
    return ".".join(p.relative_to(base).with_suffix("").parts)

def pkg(p): return fqcn(p).rsplit(".",1)[0]

@dataclass(frozen=True)
class Meta:
    old:Path; new:Path; old_fqcn:str; new_fqcn:str; name:str; new_pkg:str

def main():
    files=sorted(MAIN.rglob("*.java"))+sorted(TEST.rglob("*.java"))
    metas=[Meta(p,new_path(p),fqcn(p),fqcn(new_path(p)),p.stem,pkg(new_path(p))) for p in files]
    originals={m.old:m.old.read_text(encoding="utf-8") for m in metas}
    fqmap={m.old_fqcn:m.new_fqcn for m in metas}
    unique={}
    for m in metas: unique[m.name]=m if m.name not in unique else None

    rendered={}
    for m in metas:
        s=originals[m.old]
        for old,new in fqmap.items():
            if old!=new: s=s.replace(old,new)
        s=re.sub(r"^import com\.example\.the_cheaper\.[\w.]+\.\*;\s*\n","",s,flags=re.M)
        s,count=re.subn(r"^package\s+[\w.]+;",f"package {m.new_pkg};",s,count=1,flags=re.M)
        if count!=1: raise RuntimeError(f"missing package declaration: {m.old}")
        additions=[]
        for name,target in unique.items():
            if not target or target==m or target.new_pkg==m.new_pkg: continue
            if not re.search(rf"\b{re.escape(name)}\b",s): continue
            stmt=f"import {target.new_fqcn};"
            if stmt in s or target.new_fqcn in s: continue
            if re.search(rf"^import\s+(?!{re.escape(target.new_fqcn)};)[\w.]+\.{re.escape(name)};\s*$",s,re.M): continue
            additions.append(stmt)
        if additions:
            pm=re.search(r"^package\s+[\w.]+;\s*\n",s,re.M)
            s=s[:pm.end()]+"\n"+"\n".join(sorted(set(additions)))+"\n"+s[pm.end():]
        seen=set(); out=[]
        for line in s.splitlines():
            if line.startswith("import "):
                if line in seen: continue
                seen.add(line)
            out.append(line)
        rendered[m.new]="\n".join(out)+("\n" if s.endswith("\n") else "")

    for m in metas:
        if m.old==m.new: continue
        m.new.parent.mkdir(parents=True,exist_ok=True)
        subprocess.run(["git","mv",str(m.old.relative_to(ROOT)),str(m.new.relative_to(ROOT))],cwd=ROOT,check=True)
    for p,s in rendered.items():
        p.parent.mkdir(parents=True,exist_ok=True); p.write_text(s,encoding="utf-8")

    doc=ROOT/"docs/architecture/MODULE_STRUCTURE.md"; doc.parent.mkdir(parents=True,exist_ok=True)
    doc.write_text("""# Module-first package structure

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
""",encoding="utf-8")

    leftovers=[]
    for d in ["controller","dto","entity","mapper","repository","service","exception","external","annotation"]:
        p=MAIN/d
        if p.exists(): leftovers += [str(x.relative_to(ROOT)) for x in p.rglob("*.java")]
    if leftovers: raise RuntimeError("legacy package leftovers:\n"+"\n".join(leftovers))
    subprocess.run(["git","diff","--check"],cwd=ROOT,check=True)
    print(f"manifest: {len(metas)} java files; {sum(m.old!=m.new for m in metas)} moved")

if __name__=="__main__": main()
