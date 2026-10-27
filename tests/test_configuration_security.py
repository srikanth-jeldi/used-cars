#!/usr/bin/env python3
"""Regression checks for credential handling in committed configuration.

Run: python3 -m unittest discover -s tests -p 'test_*.py'
These checks are static guardrails, not a substitute for a secret-history scan.
"""
from pathlib import Path
import re
import unittest

ROOT = Path(__file__).resolve().parents[1]
SERVICES = ("auth-service", "car-listing-service", "chat-service", "gateway-service", "notification-service")
SENSITIVE = re.compile(r"^\s*(password|secret|token|accessKey|secretKey):\s*(.*?)\s*$", re.I)

class ConfigurationSecurityTests(unittest.TestCase):
    def test_credentials_use_environment_variables(self):
        checked = 0
        for service in SERVICES:
            for yaml in (ROOT / service / "src/main/resources").glob("application*.yaml"):
                for number, line in enumerate(yaml.read_text(encoding="utf-8").splitlines(), 1):
                    match = SENSITIVE.match(line)
                    if match:
                        checked += 1
                        with self.subTest(file=str(yaml.relative_to(ROOT)), line=number):
                            value = match.group(2).split("#", 1)[0].strip().strip('"').strip("'")
                            self.assertRegex(value, r"^\$\{[A-Z][A-Z0-9_]*(?::[^}]*)?\}$")
        self.assertGreater(checked, 0, "No credential settings inspected")

    def test_compose_does_not_embed_database_password(self):
        content = (ROOT / "docker-compose.yaml").read_text(encoding="utf-8")
        self.assertIn("MYSQL_ROOT_PASSWORD: ${DB_PASSWORD:?", content)
        self.assertIn("127.0.0.1:3307:3306", content)

    def test_no_basic_auth_in_eureka_url(self):
        for service in SERVICES:
            for yaml in (ROOT / service / "src/main/resources").glob("application*.yaml"):
                with self.subTest(file=str(yaml.relative_to(ROOT))):
                    self.assertNotRegex(yaml.read_text(encoding="utf-8"), r"https?://[^\s/@]+:[^\s/@]+@")

if __name__ == "__main__":
    unittest.main()
