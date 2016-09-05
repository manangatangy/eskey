There are two options for accessing the database; 1) using a contentProvider and a CursorLoader or
2) using a custom Loader that goes directly to the sqllite database without a content provider.
Both approaches required a loaderManager.
Adding the encryption step to these approaches requires 1) a new AsyncTask or 2) reuse the AsyncTask
in the custom loader.

