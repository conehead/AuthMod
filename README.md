AuthMod
=======

This is a Bukkit plugin that, in conjunction with the AuthMod client mod, authenticates users on a server with use of an RSA certificate. This is useful if passwords of Minecraft users are compromised on your server, especially high-level staff.


Setting up AuthMod
------------------

The only permission to set up is `auth.required`. If that permission is set and a player with that permission connects, they will be kicked if they don't have AuthMod with the correct certificate. Because this uses the new Plugin Channels, there is no crashing of the client or the server if either doesn't have the Mod/Plugin.

Usage
-----

The first time AuthMod is loaded, it will generate a private and public key, stored as private.key and public.key in the root directory of the server, respectively. After the key is generated, make a copy of public.key named authmod.key, and give this to your AuthMod users to put directly in their .minecraft folder.

If at any point the key is compromised, simply delete both private.key and public.key. New ones will be generated the next time the plugin loads.