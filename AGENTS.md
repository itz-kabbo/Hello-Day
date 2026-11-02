# Workspace Rules: Shift Handoff Protocol

## Shift Initialization & Context Continuity
- **Mandatory First Step:** Whenever the user says "start my shift", starts a new conversation, or sends any message initiating work in a new conversation, the agent **MUST** automatically read `HANDOFF.md` before taking action.
- **Context Awareness:** Review `HANDOFF.md` for completed milestones, open issues/bugs, architecture notes, and recommended next steps.
- **Reporting:** Briefly acknowledge the latest handoff state (last completed shift, build status, pending next steps) and ask or confirm what the focus should be for the current session.
