> *You can make classes, but you can only ever make one instance of them. This shouldn't affect how
most object-oriented programmers work. - [TodePond](https://github.com/TodePond/DreamBerd---e-acc?tab=readme-ov-file#classes)*

# Mercury Pipeline


Service that updates Yandex Tracker issues based on GitLab Merge Request events.

## Prerequisites

Before using Mercury Pipeline, make sure to:

1. Obtain a Yandex Tracker API **token** and **organization id**. Refer to the [Yandex Tracker documentation](https://cloud.yandex.ru/ru/docs/tracker/concepts/access) for details.

2. Set up a webhook in GitLab to trigger Mercury Pipeline:
    - Navigate to Repository -> Settings -> Webhooks.
    - Add a new webhook:
        - **URL**: `<application-host>/merge-requests`.
        - **Trigger**: Merge request events.

## Build and Deployment

### The Simplest Way

1. Download the latest `app.jar` from the [latest release](https://github.com/butvinm/mercury-pipeline/releases/latest).
2. Create a `share` directory and place the `config.yml` file in it. Refer to the [Config](#config) section for configuration details.
3. Run the following command:
```bash
java -jar app.jar \
    --share="./share" \
    --config="config.yml" \
    --yt.token="your-token" \
    --yt.orgId="your-organization-id"
```

### ~~For zoomers~~ In Docker

1. Create a `.env` file and fill in the credentials:
```dotenv
YT_TOKEN="your-token"
YT_ORGID="your-organization-id"
SHARE=/share
CONFIG=config.yml
```

2. Build the Docker image:
```bash
scripts/build.sh
```

3. Run the Docker container:
```bash
scripts/run.sh
```

## Config

The [config.yml](./share/config.yml) file allows you to configure your pipeline according to your requirements.

### `mr_name_pattern`

Specify the pattern to determine the Yandex Tracker issue ID related to a specific merge request.

For example, if your merge requests are prefixed with an issue ID (e.g., "168 Add something essential"), set the following value:
```yaml
mr_name_pattern: "(?<issueId>[\d]+).+"
```

### `transitions`

Define how pipeline events affect tasks in Yandex Tracker using the `transitions` section.

Basic structure:
```yaml
transitions:
  - when:
      - filter 1
      - filter 2
    status: transition name

  - when:
      - filter 3
      - filter 4
    status: transition name
```

### Available filters

#### `new_review`: boolean

Triggered when a new reviewer is assigned.

#### `new_label`: string

Triggered when a label with the specified name is assigned.

#### `del_label`: string

Triggered when a label with the specified name is removed from the merge request.

#### `mr_state`: "open", "close", "reopen", "update", "approved", "unapproved", "approval", "unapproval", "merge"

Triggered when the merge request state changes.


## Write your own handlers

TODO

<!-- If you need custom feature, you can modify codebase. Fortunately, we have some auxiliary for that.

There are [EventHandler](src/main/java/butvinm/mercury/pipeline/handler/EventHandler.java) and [Filter](src/main/java/butvinm/mercury/pipeline/handler/Filter.java) classes, that you can use to easily attach actions to different pipelines events. -->
