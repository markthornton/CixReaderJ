# Java Reader for CIX

Currently in very preliminary stages.

The tests tend to use my own cix account (though the password is NOT in this code). Passwords and other secrets are 
managed by the Secrets class. The secrets are encrypted using AES with a master key.

The StandardPaths class is used to locate configuration and data files in OS appropriate locations.
Currently only has a Linux implementation.

I am using the new java.net.http package for access to the API, mostly with asynchronous techniques.
Learning how to use this package was part of the purpose.

Yes, there should be more JavaDoc (or even some), but it is very early days.

