# 🧪 Add Unit Tests for GifAdapter

## Description
This PR addresses the missing test coverage for `GifAdapter`.

* 🎯 **What:** Added comprehensive unit tests for `GifAdapter` utilizing Robolectric and AndroidX testing frameworks.
* 📊 **Coverage:** The test suite now covers:
  * Initial state validation.
  * Correct calculation of item counts depending on the `showLoadMore` state.
  * Proper view type assignment (`TYPE_GIF` vs `TYPE_LOAD_MORE`).
  * List manipulation logic (`setGifs`, `appendGifs`, `clearGifs`).
  * View holder creation correctness.
  * Interaction testing for item clicks and "load more" clicks.
* ✨ **Result:** Improved test coverage and reliability for the `GifAdapter`, allowing for confident refactoring without regressions.

Additionally, required test dependencies were added to `app/build.gradle` and `.gitignore` was updated to properly ignore build artifacts.
