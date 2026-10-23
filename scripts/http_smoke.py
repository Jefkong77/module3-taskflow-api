#!/usr/bin/env python3
"""Exercise the running API. Start with a fresh server; uses only Python stdlib."""
import json
import os
from pathlib import Path
from urllib.error import HTTPError
from urllib.request import Request, urlopen

base = os.environ.get("BASE_URL", "http://localhost:8080").rstrip("/")
results = []


def call(method, path, expected, body=None, raw=None):
    data = raw.encode() if raw is not None else (
        json.dumps(body).encode() if body is not None else None)
    headers = {"Content-Type": "application/json"} if data is not None else {}
    request = Request(base + path, data=data, headers=headers, method=method)
    try:
        response = urlopen(request, timeout=15)
    except HTTPError as error:
        response = error
    text = response.read().decode()
    value = json.loads(text) if text else None
    result = {"method": method, "path": path, "expected_status": expected,
              "actual_status": response.status, "response": value}
    results.append(result)
    assert response.status == expected, result
    return value, response.headers


def main():
    tasks, _ = call("GET", "/api/tasks", 200)
    assert tasks == [], "Use a freshly restarted server for this check"
    first, headers = call("POST", "/api/tasks", 201,
                          {"id": 900, "title": " First task ", "completed": False})
    first_id = first["id"]
    assert first == {"id": 1, "title": "First task", "completed": False}
    assert headers["Location"].endswith("/api/tasks/1")
    second, _ = call("POST", "/api/tasks", 201, {"title": "Second task"})
    assert second["id"] != first_id and second["completed"] is False
    all_tasks, _ = call("GET", "/api/tasks", 200)
    assert len(all_tasks) == 2
    read, _ = call("GET", f"/api/tasks/{first_id}", 200)
    assert read == first
    updated, _ = call("PUT", f"/api/tasks/{first_id}", 200,
                       {"id": 999, "title": "Updated task", "completed": False})
    assert updated["id"] == first_id and updated["title"] == "Updated task"
    for _ in range(2):
        done, _ = call("PUT", f"/api/tasks/{first_id}/complete", 200)
        assert done["completed"] is True
    saved, _ = call("GET", f"/api/tasks/{first_id}", 200)
    assert saved["completed"] is True
    for title in [None, "", "   ", "x" * 201]:
        problem, _ = call("POST", "/api/tasks", 400, {"title": title})
        assert problem["status"] == 400 and problem["detail"]
    call("POST", "/api/tasks", 400, raw='{"title":')
    for bad_id in ["abc", "0", "-1"]:
        call("GET", f"/api/tasks/{bad_id}", 400)
    for method, suffix, body in [
        ("GET", "", None), ("PUT", "", {"title": "Missing"}),
        ("DELETE", "", None), ("PUT", "/complete", None)]:
        call(method, "/api/tasks/999999999" + suffix, 404, body)
    call("DELETE", f"/api/tasks/{first_id}", 200)
    call("GET", f"/api/tasks/{first_id}", 404)
    call("DELETE", f"/api/tasks/{second['id']}", 200)
    remaining, _ = call("GET", "/api/tasks", 200)
    assert remaining == []
    print(f"Passed {len(results)} HTTP requests and response assertions")


if __name__ == "__main__":
    passed = False
    try:
        main()
        passed = True
    finally:
        output = Path(__file__).resolve().parents[1] / "docs/evidence/http-checks.json"
        output.write_text(json.dumps({"passed": passed, "base_url": base,
                                     "requests": results}, indent=2) + "\n")
