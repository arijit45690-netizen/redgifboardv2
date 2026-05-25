🧪 Testing improvement for GifAdapter

🎯 **What:** The testing gap in `GifAdapter` was addressed by adding tests for the `appendGifs` method to ensure it correctly calculates the insertion range and updates the internal list of items, while notifying observers correctly.

📊 **Coverage:** Covered appending items to an empty list, appending items to an existing list, the empty list edge case, and checking the correct item count when `showLoadMore` is true.

✨ **Result:** Enhanced unit testing reliability and test coverage for the core list loading mechanism by utilizing Robolectric to properly intercept and verify RecyclerView.Adapter callbacks.
