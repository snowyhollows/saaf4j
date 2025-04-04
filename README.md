# saaf4j: Simple Adaptive Audio Facade 4 Java

There are multiple technologies for creating adaptive, dynamic audio for interactive apps, like games:

- FMOD Studio
- Wwise
- Faust
- Pure Data
- Supercollider
- Max/MSP, especially since RBNO

Some are free, some are not. Some are open source, some are not. Neither is native to Java, but some of them contain
code generators which can be forced to generate Java code, JavaScript code, or both.

The common factor is the workflow: the audio design is done outside the game engine, using specialized tools
or languages. Then the audio can be packaged as a separate asset (in some cases - organized into multiple banks,
to facilitate smart loading of resources), and the communication between the audio engine and the host app is handled by
sending events and writing / reading exposed parameters.

saaf4 (still in its infancy), is meant as a testing ground for building:

- a minimalistic, but useful facade for adaptive audio, making it easy to use some of those engines from Java;
- a set of swappable backends, for different audio technologies:
    - the ambition is to support multiple Java platforms, such as LibGDX, JavaFX, Android, GWT...
    - the effort should also cover establishing ways of managing builds of hybrid Java project, that include, e.g. Faust
      code.
- some day, maybe, creating a saaf4j specific backend in pure, idiomatic Java code.

## How usable it is right now? What is ready?

The API is - on purpose - the smallest API possible. I plan to expand it very slowly, while adding more backends.

But since the FMOD Studio backend is already working, Java projects (especially LibGDX) projects can already benefit
from the full power of the FMOD Studio engine.

### FMOD Studio backend specifics

FMOD Studio is not free software, it is a commercial product.

There is a generous licensing for small businesses and indy developers, including the free tier for products that don't
pass certain budget constraints, and for individuals and companies earning less than certain amount per year. This is
something that everyone needs to check for themselves, for legal reasons; last time I checked - using the library in a
game, commercial or not, was free for any individual making less than $200K per year. For those above the treshold, the
licensing costs are also reasonable.

Also, the licensing specifically forbids me from publishing the binaries (I could, if I included them in a game or
another product, but I am not allowed to do so in a game engine or a general library). This is why any user needs to
register on http://fmod.com and download all the relevant SDKs (it's easiest to pluck the natives from the Unreal SDK
zips, and the HTML version from the "custom" HTML5 zip)

## Any docs on how to actually use it? Or just talking?

Just talking for now. Probably the quickest way will be starting a LibGDX project using the
official liftoff generator, but for now it does not support the saaf4j extension.
