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

| Type | Description | Typical Scope | Triggers Build | Minor or Patch<sup>1</sup> |
| --- | --- | --- | --- | --- |
| revert | Revert to a previous commit version | project | yes | minor |
| feat | Add a new feature | code | yes | minor |
| more | Add code for a future feature (later inidicated as complete with 'feat').  Supports branch abstraction and feature flags in Trunk-Based Development (TBD). | code | yes | minor |
| change | Change implementation of existing feature | code | yes | patch |
| remove | Remove a feature | code | yes | minor |
| less | Remove code for a feature (already indicated as removed with 'remove').  Supports branch abstraction and feature flags in Trunk-Based Development (TBD). | code | yes | minor |
| deprecate | Indicate some code is deprecated | code | yes | patch |
| fix | Fix a defect (e.g., bug) | code | yes | patch |
| refactor | Rewrite and/or restructure code without changing behavior | code | no | patch |
| perf | Improve performance, as a special case of refactor | code | yes | minor |
| security | Improve security aspect | code | yes | minor |
| style | Does not affect the meaning or behavior | code | no | patch | patch |
| test | Add or correct tests | code | no | patch |
| docs | Affect documentation | project, code, document (e.g., README), etc. | no | patch |
| build | Affect build components like the build tool | project, code | no | patch |
| vendor | Update version for dependencies and packages | project, code, yes | patch |
| ci | Affect CI pipeline | project, code | no | patch |
| ops | Affect operational components like infrastructure, deployment, backup, recovery, etc. | project, code | yes | patch |
| chore | Miscellaneous commits, such as updating .gitignore | project, code | no | patch |

*1 - Unless indicated as a breaking change, then is 'major'*


# License
The java-util project is released under [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)
