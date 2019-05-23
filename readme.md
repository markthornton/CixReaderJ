# Java Reader for CIX

Currently in very preliminary stages.

Configuration data is stored (on Linux) in <home>/.config/uk.me.mthornton/CixReaderJ.json. Passwords are stored separately (AES encrypted).
Set configure your own user name and password, run the uk.me.mthornton.cix.auth.SetCixCredentials application. This will then be
used by any tests requiring it.

The StandardPaths class is used to locate configuration and data files in OS appropriate locations.
Currently only has a Linux implementation.

I am using the new java.net.http package for access to the API, mostly with asynchronous techniques.
Learning how to use this package was part of the purpose.

Yes, there should be more JavaDoc (or even some), but it is very early days.

