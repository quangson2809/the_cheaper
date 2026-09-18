"""Fail closed if required suites, successful Newman execution or non-skipped tests are missing."""
import json
import os
from pathlib import Path
import subprocess
import sys
import xml.etree.ElementTree as ET

root = Path(__file__).resolve().parents[1]
reports = root / "build/test-results/test"
suites = []
for file in sorted(reports.glob("TEST-*.xml")):
    node = ET.parse(file).getroot()
    suites.append({"name": node.attrib["name"], **{
        key: int(node.get(key, "0")) for key in ("tests", "failures", "errors", "skipped")}})
required = {"Phase5OrderSecurityIntegrationTest": 73,
            "Phase5PostmanIntegrationTest": 1, "Phase5RestartIntegrationTest": 1}
problems = []
if not suites:
    problems.append("No JUnit reports")
for name, count in required.items():
    found = [s for s in suites if s["name"].endswith("." + name)]
    if len(found) != 1 or found[0]["tests"] < count:
        problems.append(f"Required suite missing/incomplete: {name} ({count} cases required)")
totals = {key: sum(s[key] for s in suites) for key in ("tests", "failures", "errors", "skipped")}
if any(totals[key] for key in ("failures", "errors", "skipped")):
    problems.append("JUnit has failures/errors/skipped cases")
newman_file = root / "build/phase5/newman.json"
newman = {}
if not newman_file.exists():
    problems.append("Newman report missing")
else:
    run = json.loads(newman_file.read_text(encoding="utf-8"))["run"]
    newman = {"stats": run["stats"], "failureCount": len(run["failures"])}
    expected_requests = len(json.loads((root / "docs/postman/TheCheaper-Order-RBAC.postman_collection.json").read_text(encoding="utf-8"))["item"])
    if (run["failures"] or run["stats"]["requests"]["total"] != expected_requests
            or run["stats"]["assertions"]["total"] < expected_requests):
        problems.append("Newman failed or did not execute the complete collection")
sha = os.environ.get("GITHUB_SHA") or subprocess.check_output(["git", "rev-parse", "HEAD"], cwd=root, text=True).strip()
result = {"commit": sha, "runUrl": (f"https://github.com/{os.environ['GITHUB_REPOSITORY']}/actions/runs/{os.environ['GITHUB_RUN_ID']}"
          if os.environ.get("GITHUB_RUN_ID") else None), "totals": totals, "suites": suites,
          "newman": newman, "accepted": not problems, "problems": problems}
out = root / "build/phase5/evidence.json"
out.parent.mkdir(parents=True, exist_ok=True)
out.write_text(json.dumps(result, indent=2) + "\n", encoding="utf-8")
print(json.dumps({"commit": sha, **totals, "newman": newman, "accepted": not problems, "problems": problems}))
sys.exit(bool(problems))
