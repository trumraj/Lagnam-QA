# Slack `/run-tests` trigger

Lets anyone in a Slack channel run this project's Playwright/Cucumber suite
(`mvn test`) by typing `/run-tests`, and post the result back to the channel.

Code lives in `src/main/java/slack/`:

- `SlackConfig` — reads settings from environment variables.
- `SlackSignatureVerifier` — verifies the request really came from Slack.
- `MavenTestRunner` — shells out to `mvn test` (optionally with a Cucumber
  tag filter) and summarizes the result.
- `SlackResponder` — posts the result back to Slack's `response_url`.
- `SlackCommandServer` — the HTTP server that ties it all together.

## 1. Create the Slack app

1. Go to https://api.slack.com/apps -> **Create New App** -> **From scratch**.
2. Pick a name (e.g. "QA Test Runner") and your workspace.
3. Under **Features -> Slash Commands**, click **Create New Command**:
   - Command: `/run-tests`
   - Request URL: `https://<your-public-host>/slack/run-tests`
   - Short description: "Run the Lagnam QA automation suite"
4. Under **Settings -> Basic Information -> App Credentials**, copy the
   **Signing Secret** — you'll need it below.
5. Install the app to your workspace (**Settings -> Install App**).

## 2. Expose the server publicly

Slack needs an HTTPS URL it can reach. For local development, use ngrok:

```bash
ngrok http 4567
```

Copy the `https://....ngrok-free.app` URL and use
`https://....ngrok-free.app/slack/run-tests` as the slash command's Request
URL (step 1.3 above). For a permanent setup, deploy this server somewhere
with a stable HTTPS endpoint (behind your usual reverse proxy/TLS) instead.

## 3. Configure environment variables

| Variable               | Required | Default              | Purpose                                   |
|-------------------------|:--------:|----------------------|--------------------------------------------|
| `SLACK_SIGNING_SECRET`  | yes      | —                     | Verifies requests actually came from Slack |
| `SLACK_SERVER_PORT`     | no       | `4567`                | Port the webhook server listens on         |
| `SLACK_SLASH_COMMAND`   | no       | `/run-tests`          | Must match the command configured in Slack |
| `SLACK_PROJECT_DIR`     | no       | current directory     | Where `mvn test` is executed               |
| `SLACK_MAVEN_GOAL`      | no       | `test`                | Maven goal to run                          |

Never commit the signing secret. Export it in your shell instead:

```bash
export SLACK_SIGNING_SECRET="paste-the-signing-secret-here"
```

## 4. Run the server

```bash
mvn exec:java -Dexec.mainClass=slack.SlackCommandServer
```

You should see:

```
Slack command server listening on port 4567 for /run-tests
```

## 5. Use it

In any channel the app is added to, type:

```
/run-tests
```

The bot immediately replies that the run started, then runs `mvn test`
in the background and posts a pass/fail summary (exit code + the
Cucumber "Scenarios/Steps" summary lines) back to the channel once it
finishes.

Optional: pass a Cucumber tag expression as the command's text to run a
subset of scenarios, e.g.:

```
/run-tests @smoke and not @wip
```

Only one run is allowed at a time — if `/run-tests` is invoked while a
run is already in progress, Slack is told to wait rather than starting a
second concurrent `mvn test`.
