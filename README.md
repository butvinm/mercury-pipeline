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
2. Create a `share` directory and place the [config.yml](./share/config.yml) file in it. Refer to the [Config](#config) section for configuration details.
3. Run the following command:
```bash
java -jar app.jar \
    --share="./share" \
    --config="config.yml" \
    --yt.token="your-token" \
    --yt.orgId="your-organization-id"
```

### ~~For zoomers~~ In Docker

1. Create a `.env` (or just rename `.example-env`) file and fill in the credentials:
```dotenv
YT_TOKEN="your-token"
YT_ORGID="your-organization-id"
SHARE="/share"
CONFIG="config.yml"
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

Pattern for extracting Yandex Tracker issue ID related to nerge request from its name.

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
      new_reviewer: true
    status: in_review

  - when:
      new_label: rejected
    status: close
    resolution: later
```

#### `when`

> `new_reviewer`: boolean
>
> Triggered when a new reviewer is assigned.

> `new_label`: string
>
> Triggered when a label with the specified name is assigned.

> `del_label`: string
>
> Triggered when a label with the specified name is removed from the merge request.

> `mr_state`: "open", "close", "reopen", "update", "approved", "unapproved", "approval", "unapproval", "merge"
>
> Triggered when the merge request state changes.

#### `state`

Actually, transition, that would be applied for an issue.

#### `resolution`: "fixed", "wontFix", "cantReproduce", "duplicate", "later"

Resolution for such transitions as "close". Default "fixed".

## Write your own handlers

If your project has specific requirements that go beyond the default behavior, you can easily define custom handlers in the [`customExecutor()`](src/main/java/butvinm/mercury/pipeline/PipelineApplication.java) method. This allows you to tailor the execution of tasks based on your unique needs.

### Fancy builder

> If you're that *experienced java developer who has already seen enough of such things*, just skip to boring builder bellow.

We provide a sophisticated builder that closely resembles a configuration file, simplifying the definition of a custom executor. This builder includes a triggers section, analogous to transitions, allowing you to execute custom functions on specific events.

Example:
```java
private Executor customExecutor(ExecutorConfig config) throws DefinitionException {
    return Executor.definition(yt)
    .mrNamePattern(config.getMrNamePattern().pattern())
    .transitions()
        .when()
            .newReviewer()
        .status("in_review")

        .when()
            .newLabel("rejected")
            .mrState(MRState.CLOSE)
        .status("need_info")

        .when()
            .delLabel("rejected")
        .status("close")
        .resolution(Resolution.LATER)
    .triggers()
        .when()
            .newReviewer()
        .action(this::notifyReviewer)
    .define();
}
```

> btw, we have common boring builders as well (`Executor.builder()`, `Trigger.builder()`, `Transaction.builder()`, `Filter.builder()`). But why do you need them?
