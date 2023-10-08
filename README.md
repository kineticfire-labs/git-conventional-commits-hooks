# git-conventional-commit-hooks
[![Powered by KineticFire Labs](https://img.shields.io/badge/Powered_by-KineticFire_Labs-CDA519?link=https%3A%2F%2Flabs.kineticfire.com%2F)](https://labs.kineticfire.com/)
[![License: Apache 2.0](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
<p></p>
Git hooks to format and enforce Conventional Commit rules for git commit messages

Server-side and client-side hooks to format and enforce [Conventional Commit rules](https://www.conventionalcommits.org/en/v1.0.0/) for git messages and support [Semantic Versioning](https://semver.org/) in a Continuous Integration & Continuous Delivery/Deployment pipeline.

# Description

todo a change for testing git commit hook

**Generic Scopes**

| Generic Scope | Description |
| --- | --- |
| project | Applies to entire project |
| code | Application, library, container image, Ansible playbooks (infrastructure), etc. |
| document | README, user guide, developer guide, etc. |

**Type Description**

| Type | Typical Scope Applicability | Description |
| --- | --- | --- |
| revert | project | Revert to a previous commit version |
| feat | code | Add a new feature |
| fix | code | Fix a defect (e.g., bug) |
| refactor | code | Rewrite and/or restructure code without changing behavior |
| perf | code | Improve performance, as a special case of refactor |
| style | code | Does not affect the meaning or behavior |
| test | code | Add or correct tests |
| docs | project, document (e.g., README), etc. | Affect documentation |
| build | project, code | Affect build components like the build tool |
| vendor | project, code | Update version for dependencies and packages |
| ci | project | Affect CI pipeline |
| ops | project, code | Affect operational components like infrastructure, deployment, backup, recovery, etc. |
| chore | project | Miscellaneous commits, such as updating .gitignore |



# License
The java-util project is released under [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)
