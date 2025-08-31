### FAY ANDROID TAKE-HOME PROJECT

## Instructions
* Open the fay-android project with Android Studio
* Build and install the app module configuration to your target emulator or device
* Play around with the app!
    - Try logging in with invalid credentials to see the error handling
    - Once logged in, notice how the app preserves authentication across cold starts
    - The UI should be relatively responsive on configuration changes, although I focused on developing for phones
    - Toggle dark mode and light mode, plus try dynamic theming if your device supports it
    - Turn on airplane mode and attempt to refresh appointments - watch how the error states are handled
    - Turn airplane mode back off and refresh again to see the seamless recovery
    - Navigate between sections using the bottom navigation - notice how state is preserved
    - Log out from the profile screen and observe the clean transition back to login
* Run the unit tests
* Browse the code
* Watch the demo (video)[https://drive.google.com/file/d/1zBwQpreh-OMbkSV74_tMB0h-16BrCv1k/view?usp=drive_link],
  where I screen recorded myself walking through some of the user journeys and testing authentication and network connectivity cases.

## Notes
Here is a brief explanation of my choices of dependencies:

- DI: Hilt, Dagger
  I've been advocating for Hilt in production codebases for a while now, particularly because traditional Dagger setups
  tend to become unwieldy monsters. The multi-module architecture in this project really showcases where Hilt shines. Each feature
  module can define its own dependencies without stepping on other modules' toes, and scoping dependencies is much more straightforward.

- Network/Data: Retrofit, OkHttp, Kotlinx Serialization
  I chose Retrofit, which uses OkHttp under the hood, along with Kotlin Serialization as my converter of choice,
  because they are fairly ubiquitous in the industry, and are what I'm most familiar with professionally.
  The custom NetworkConnectivityInterceptor is something I like to add to projects because it catches connection issues
  before they hit the repository layer, allowing for immediate user feedback. I chose Kotlin Serialization over Gson or Moshi
  mostly because it is Kotlin-first, it is reusable for things like type-safe Compose Navigation, and provides compile-time safety.

- UI: Jetpack Compose, Material3
  Compose makes it so easy to write declarative code that has clear state in reaction to emitted changes. I used Material3 partially because
  I haven't gotten to use it before in a production codebase, but also because it allows for dynamic theming!

- Storage: DataStore
  DataStore over SharedPreferences was a deliberate choice for the authentication token. The Flow-based API means
  authentication state changes are immediately reflected throughout the app - no polling, no manual checks, just
  reactive authentication that works.

Here is a brief explanation of my architecture choices:

- I used a modular structure, representative of a real-world codebase, albeit with less package separation.
  I split individual features and core pieces of functionality into separate modules, and aimed to maintain
  a healthy separation of concerns by largely following clean architecture principles with, for the most part, proper dependency inversion.
  I used a version catalog to make declaring plugins and dependencies easier and compile-time safe.

- I employed the repository pattern, separating the domain layer from the presentation layer, and used a view model,
  following the MVVM architecture pattern, to convert data from the domain layer to view state to be used to create the UI,
  making sure that proper separation of concerns between the data layer and the view model was maintained.
  One of my goals was to write instrumented tests to test the view model and the UI, but decided against it for the sake of time.

- I used Kotlin coroutines, Flows, and StateFlows in this exercise to ensure that asynchronous operations are handled by the proper dispatcher
  and within the proper context and scope, and that they are testable. The UI collects the StateFlow exposed by the view model
  and recomposes as necessary when new emissions are received, while also being lifecycle-aware.

- I implemented a simple NavHost showing type-safe navigation in a single-Activity architecture. While most of the screens were stubbed,
  it was still cool to be able to demonstrate the backstack management and state restoration that NavHostController provides.

- My testing strategy focuses on the data layer because that's where most of the complexity lives. Every repository
  has comprehensive tests covering success paths, HTTP errors, network failures, and edge cases like null responses.
  I generally prefer to create a very robust data and domain layer, making UI issues much easier to isolate.

Here is a brief explanation of some of the challenges I faced on this project:

- I spent a bit more time than expected on the authentication flow for a couple of reasons. One, I wanted to make sure error handling
  was robust, but I needed to create an accurate way to notify the UI of authentication status to facilitate navigation actions
  and to ensure that the user always saw the workflow they would expect. Once I got that working, I needed to make sure the auth token
  was properly persisted and cleared on login/logout, so that the user wouldn't have to login on every cold start. I added a logout button
  to the Profile tab to be able to test the flow in different ways, which was fun! The biggest challenge came when launching the app with
  an existing auth token would show the LoginScreen momentarily before moving to the AppointmentsScreen. This was because of the timing of
  the initial value of the flow being returned before the datastore fetch returned and emitted the most recent value. This led me to implement
  the SplashScreen API in the app to allow me to set a wait condition to ensure I did not compose the app's UI until that value was present.

- I wanted to create a modular app to showcase a simpler version of my approach to app architecture. Usually in take-home projects, you only have
  several hours to put the project together, so I usually end up using packages in the app module to create a microcosm of how a modular codebase
  would look. This time though, I felt like putting my best foot forward meant tackling the additional overhead to structure it properly.
  Although I was able to save some time using AI to stub or duplicate some of the common module patterns, it was still a more time-consuming process
  than I anticipated to break everything out and ensure the dependencies between modules made sense, particularly around shared core modules and
  what should be an Android library module vs a pure Kotlin module. Having worked in modular codebases, shared build logic via convention plugins
  makes things so much cleaner, but that was a lower priority than other aspects of the project, and something I would likely tackle with more time.

- I definitely wanted to include more test coverage, especially of the various ViewModels in the codebase, but for the sake of time I stuck to
  primarily testing the data and domain layers. I used AI to generate the tests, so they are not as clean and encapsulated and abstracted into
  shared logic and patterns as I would normally make them. That being said, I think the coverage on the classes I did test is great!

Here is my approximation on how much time I spent on each major area of the project:

- Login screen: The screen itself was fairly easy, but including time spent on authentication state management and related edge cases, probably 4+ hours
- Appointments screen: The UI for this screen was the most complex, but including the data/domain layers and proper timezone conversion, probably 4+ hours
- Nice-to-haves: Unit testing, theming, navigation, resources, network connectivity and error handling, probably 2 hours in aggregate
- Any additional time spent: probably another 3 hours or so spent on modularization and dependency optimization, plus this write-up

Here is what I would have done with more time or how I would continue to enhance this project if it was a production app:

- I traded UI polish somewhat for error handling, network connectivity checks, and better state management for edge cases.
- For the sake of time, I left my network connectivity check as an interceptor on network calls, but would have liked to make network changes observable and act accordingly.
- With more time, I would definitely like to build out the navigation graph and UI
- With more time, and to make this app more production ready, I would love to properly configure Proguard/R8 for the codebase, and create additional build types that use proper minification/obfuscation configuration.
- With more time, I would love to add tracing and analytics to capture user interaction as well as network usage and connectivity changes. For that, I would typically use Sentry and DataDog SDKs.

Final notes:

* I added code comments to explain important methods and properties as necessary, citing sources where applicable.
* I hope you enjoy reading my code. I strive for concise and minimalistic code, while holding myself to a high standard of organization, efficiency, and testability.
* Although the scope of this exercise does not require a fully polished app, I hope I've been able to showcase some of my skills, as well as the thought process behind them.
* I definitely spent more time on this project than I normally would on a take-home, but what I hope sets me apart is that I've created something that is closer to production-ready
  and something that can easily scale, given the foundations put in place. I look forward to hearing the team's feedback!

-Benjamin Piatt