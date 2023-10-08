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

| Type | Typical Scope Applicability | Description | Triggers Build ? | Minor or Patch<sup>1</sup> |
| --- | --- | --- | --- | --- |
| revert | project | Revert to a previous commit version | yes | minor |
| feat | code | Add a new feature | yes | minor |
| change | code | Changes implementation of existing feature | yes | patch |
| remove | code | Removes a feature | yes | minor |
| deprecate | code | Deprecate | yes | patch |
| fix | code | Fix a defect (e.g., bug) | yes | patch |
| refactor | code | Rewrite and/or restructure code without changing behavior | no | patch |
| perf | code | Improve performance, as a special case of refactor | yes | minor |
| security | code | Improve security aspect | yes | minor |
| style | code | Does not affect the meaning or behavior | no | patch | patch |
| test | code | Add or correct tests | no | patch |
| docs | project, document (e.g., README), etc. | Affect documentation | no | patch |
| build | project, code | Affect build components like the build tool | no | patch |
| vendor | project, code | Update version for dependencies and packages | yes | patch |
| ci | project, ci | Affect CI pipeline | no | patch |
| ops | project, code | Affect operational components like infrastructure, deployment, backup, recovery, etc. | yes | patch |
| chore | project | Miscellaneous commits, such as updating .gitignore | no | patch |

*1 - Unless indicated as a breaking change, then is 'major'*


# License
The java-util project is released under [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)
