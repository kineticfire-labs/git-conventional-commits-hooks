# git-conventional-commits-hooks
[![Powered by KineticFire Labs](https://img.shields.io/badge/Powered_by-KineticFire_Labs-CDA519?link=https%3A%2F%2Flabs.kineticfire.com%2F)](https://labs.kineticfire.com/)
[![License: Apache 2.0](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
<p></p>

Git hooks to format and enforce standardized git commit messages per [Conventional Commits specification](https://www.conventionalcommits.org/) and enable automated [semantic versioning (e.g., SemVer)](https://semver.org/) in a Continuous Integration & Continuous Delivery/Deployment pipeline.  Client-side hooks are available; server-side hooks are coming soon.

# Table of Contents
1. [Purpose](#purpose)
2. [Approach](#approach)
   1. [Standardized Commit Messages](#standardized-commit-messages)
   2. [Semantic Versioning](#semantic-versioning)
3. [License](#license)

# Purpose

git-conventional-commits-hooks aims to help developers produce standardized git commit messages and, through that, enable automated versioning and accelerate continuous delivery/deployment.

Standardized commit messages not only help a human better understand the changes introduced across commits but also removes the subjectivity of version number changes (e.g., "is the change a patch, minor, or major version number increment?").  The same standardized commit messages can be processed by automated tools, which can produce a new build with an automated version number to help accelerate the CI/CD pipeline.

# Approach

## Standardized Commit Messages

git-conventional-commits-hooks adopts the [Conventional Commits specification](https://www.conventionalcommits.org/) to achieve **standardized commit messages**.  The specification defines the format and content for a commit message.

The first line, the title line, is required and includes a *type*, *scope*, and *description*.
- *type*: The type of the commit, where *type* is an enumerated value that indicates the intent of the commit, e.g. a feature, bug fix, etc.  Required.
- *scope*: The scope of the commit, where *scope* is an enumerated value that indicates what is affected by the commit.  Required, although Conventional Commits says optional.
- *description*: Succintly describes the commit.  Required.

The optional body provides additional detail about the commit.
- If no body is provided, then the title line represents the entirety of the commit
- If a body is present, then an empty line must separate the title line from the body

A breaking change is indicated by either an exclamation point after the closing parenthesis after the scope and before the colon e.g. `(<scope>)!: <description>`, by putting `BREAKING CHANGE: <description>` into the body, or both.

The general format of a commit message, following the rules described above, is:

```
<type>(<scope>): <description>

[optional body]
```

Example 1 - title line only (no body) without breaking change:
```
docs(project): correct misspellings and typos in README
```


Example 2 - title line only (no body) with breaking change:
```
feat(api)!: must include API token in all API queries
```


Example 3 - body without breaking change:
```
feat(app): allow users to register multiple contact email addresses

User may register more than email address.  Once verified, an email
address may be indicated as 'primary' for the user to login and to
receive email communications.
```


Example 4 - body with breaking change:
```
feat(app)!: user login requires username and not email address

User login identifies the user by configurable username and no
longer accepts an email address to identify the user

BREAKING CHANGE: user login requires username, and does not accept
email address
```

The scripts provided by git-conventional-commits-hooks can help format and enforce standardized git commit messages.


## Semantic Versioning

Semantic versioning and Conventional Commits complement each other well.  Automated tools can process standardized commit messages and determine the appropriate change to the version number. [SemVer](https://semver.org/) defines a set of rules and requirements that determines how a version number is incremented.

For example for a current version of 1.2.3:
- a *style* commit will NOT trigger a build itself; once built, the new version would be 1.2.4
- a *fix* commit will trigger a build and result in a new version of 1.2.4
- a *feat* commit will trigger a build and result in a new version of 1.3.0
- a *BREAKING CHANGE* commit will trigger a build and result in a new version of 2.0.0

git-conventional-commits-hooks considers [semantic versioning with SemVer](https://semver.org/) in the definition, formatting, and enforcement of git commit messages.

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
| more | Add code for a future feature (later inidicated as complete with 'feat').  Support branch abstraction in Trunk-Based Development (TBD). | code | yes | minor |
| change | Change implementation of existing feature | code | yes | patch |
| remove | Remove a feature | code | yes | minor |
| less | Remove code for a feature (already indicated as removed with 'remove').  Support branch abstraction in Trunk-Based Development (TBD). | code | yes | minor |
| deprecate | Indicate some code is deprecated | code | yes | patch |
| fix | Fix a defect (e.g., bug) | code | yes | patch |
| refactor | Rewrite and/or restructure code without changing behavior | code | no | patch |
| perf | Improve performance, as a special case of refactor | code | yes | minor |
| security | Improve security aspect | code | yes | minor |
| style | Does not affect the meaning or behavior | code | no | patch | patch |
| test | Add or correct tests | code | no | patch |
| docs | Affect documentation | project, code, document (e.g., README), etc. | no | patch |
| build | Affect build components like the build tool | project, code | no | patch |
| vendor | Update version for dependencies and packages | project, code, etc. | yes | patch |
| ci | Affect CI pipeline | project, code | no | patch |
| ops | Affect operational components like infrastructure, deployment, backup, recovery, etc. | project, code | yes | patch |
| chore | Miscellaneous commits, such as updating .gitignore | project, code | no | patch |

*1 - Unless indicated as a breaking change, then is 'major'*


# License
The git-conventional-commits-hooks project is released under [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)
