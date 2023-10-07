# git-conventional-commit-hooks
[![Powered by KineticFire Labs](https://img.shields.io/badge/Powered_by-KineticFire_Labs-CDA519?link=https%3A%2F%2Flabs.kineticfire.com%2F)](https://labs.kineticfire.com/)
[![License: Apache 2.0](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
<p></p>
Git hooks to format and enforce Conventional Commit rules for git commit messages

Server-side and client-side hooks to format and enforce [Conventional Commit rules](https://www.conventionalcommits.org/en/v1.0.0/) for git messages and support [Semantic Versioning](https://semver.org/) in a Continuous Integration & Continuous Delivery/Deployment pipeline.

# Description

**Type Description**

| Type | Typical Scope Applicability | Description |
| --- | --- | --- | --- |
| revert | project | Reverts to a previous commit version |
| feat | code | Adds a new feature |
| fix | code | Fixes bug |
| refactor | code | Rewrites and/or restructures code, but doesn't change behavior |
| perf | code | Improves performance, as a special case of refactor |
| style | code | Does not affect the meaning or behavior |
| test | code | Adds or corrects tests |
| docs | readme, etc. | Affects documentation |
| docsInternal | internal code docs | Affects internal documentation internal |
| docsExternal | external code docs | Affects external documentation |
| build | project, code | Affects build components like the build tool |
| vendor | project, code | Update version for dependencies and packages |
| ci | project | Affects CI pipeline |
| ops | project, code | Affects operational components like infrastructure, deployment, backup, recovery, etc. |
| chore | project | Miscellaneous commits, such as updating .gitignore |



# License
The java-util project is released under [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)
