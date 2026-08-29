@AGENTS.md

# Claude-specific verification workflow

After any code update, ensure the following steps are completed before finishing:

- Review whether `test/ui-test-plan.md` needs to be updated to cover the changed behaviour.
- Invoke the `test-ui` skill to run the relevant console checks.
- If any case fails, stop immediately and report the actual vs expected output.
