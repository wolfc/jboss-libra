Libra
=====

Libra provides the means to weigh your Objects.

API
---

    long myObjectSize = Libra.getObjectSize("Hello world");

That's it. No more.

Bootstrapping
-------------

There are two ways to boot up Libra.

### Java Agent ###

    java -javaagent:jboss-libra.jar MyMain

Libra will be initialized through the Java Agent mechanism.

### tools.jar ###

    java -cp $JAVA_HOME/lib/tools.jar MyMain

Libra will initialize itself through the Attach API.

Usage in AS 7
-------------

For usage in AS 7 please refer to [How-To: use Libra in AS 7](https://github.com/wolfc/jboss-libra/wiki/How-To:-use-Libra-in-AS-7).