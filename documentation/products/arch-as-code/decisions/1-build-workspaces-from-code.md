## Context
Modifying the json version of Workspace is a poor experience.

## Decision
We will treat yml architecture data structure as the 'source' to generate and publish the Structurizr workplace 'artificat'. Structurizr Cloud Service will continue to persit state and provide a read-only view.

## Consequences
We will not checkin any workspace json files. We will not provide 2 way sync. We will not use the revision numbers in Structurizr, rather relying on git for versioning and tagging.