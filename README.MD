# KotlinConf Challenge

## Setup

### Plugin and Google Sheet configuration setup.

Before you do anything here,
follow [the steps laid out in the plugin's repository](https://github.com/brokenhappy/KotlinConfChallengePlugin?tab=readme-ov-file#setup).

### Prepare the challenge machine

#### Google Sheet ID setup

Now that the Google Sheet configuration prepared and **it is public**, we can now link it on the challenge machine.
To do that, we will download the file into the "local file cache",
which is a local copy of the configuration defined in the Google sheet.
AKA "the file that is shared between the runtime and the plugin". By default this is 
`~/Documents/filesForKotlinConfChallenge25/downloadCache.json`.

To set this up, one must define the Google Sheet ID.
The ID can be found as part of the URL of your Google Sheet (`https://docs.google.com/spreadsheets/d/<SheetId>/blabla`).
This ID should be set in [Settings > KotlinConf Challenge](jetbrains://idea/settings?name=Tools--KotlinConf+Challenge).

Now you can press `Ctrl + Opt + Cmd + R` to invoke the `Refetch Challenges` task to update the local cache file.
This should also happen automatically every minute.

#### Change the default position for Compose Hot Reload (CHR) session.

The hotRun.kt file's main function takes 4 **float** arguments to determine the
starting position of the window.
This should be configured according to the display setup and preferences.
Imagine the following scenario:
 - The main screen is 1920x1080
 - Attached monitor is 1920x1080
 - Attached monitor is on the left side

If we want to put the window in full screen on the attached monitor,
then we use the following arguments: jvmRun --args="-1920.0 0.0 1920.0 1080.0" -DmainClass=HotRunKt

Having this prepared makes it much easier to restart the sessions.

#### Change the team color

Change the `com.woutwerkman.scaffolding.teamBlue` to set whether is the red or the blue player.
This changes the color and some UI elements. Only visible to the audience.

**And then you're ready to go for it. Have fun! <3**
