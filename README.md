<img alt="logo" src="https://www.objectionary.com/cactus.svg" height="100px" />

[![EO principles respected here](https://www.elegantobjects.org/badge.svg)](https://www.elegantobjects.org)
[![DevOps By Rultor.com](http://www.rultor.com/b/objectionary/eo-sys)](http://www.rultor.com/p/objectionary/eo-sys)
[![We recommend IntelliJ IDEA](https://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)

[![mvn](https://github.com/objectionary/eo-sys/actions/workflows/mvn.yml/badge.svg?branch=master)](https://github.com/objectionary/eo-sys/actions/workflows/mvn.yml)
[![PDD status](http://www.0pdd.com/svg?name=objectionary/eo-sys)](http://www.0pdd.com/p?name=objectionary/eo-sys)
[![codecov](https://codecov.io/gh/objectionary/eo-sys/branch/master/graph/badge.svg)](https://codecov.io/gh/objectionary/eo-sys)
[![Maven Central](https://img.shields.io/maven-central/v/org.eolang/eo-sys.svg)](https://maven-badges.herokuapp.com/maven-central/org.eolang/eo-sys)
[![Hits-of-Code](https://hitsofcode.com/github/objectionary/eo-sys)](https://hitsofcode.com/view/github/objectionary/eo-sys)
![Lines of code](https://img.shields.io/tokei/lines/github/objectionary/eo-sys)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/objectionary/eo-sys/blob/master/LICENSE.txt)

[EO](https://www.eolang.org) objects for interactions with operating system.

This is how you make a [`write`](https://man7.org/linux/man-pages/man2/write.2.html) 
[syscall](https://man7.org/linux/man-pages/man2/syscall.2.html) 
to print "Hello, world" to the console (here `1` is the handle of 
[stdout](https://en.wikipedia.org/wiki/Standard_streams)):

```
[] > app
  "Hello, world!" > msg
  QQ.sys.call > @
    "write"
    1
    msg
    msg.length
```

This is how you get the current process ID using `SYS_getpid`:

```
[] > app
  QQ.io.stdout > @
    QQ.txt.sprintf
      "Current PID is %d"
      QQ.sys.call
        "getpid"
```

This is how you detect what OS you are with:

```
[] > app
  QQ.io.stdout > @
    QQ.txt.sprintf
      "This is %s"
      switch.
        * 
          QQ.sys.uname.is-windows
          "Windows"
        * 
          QQ.sys.uname.is-unix
          "Unix"
        * 
          QQ.sys.uname.is-macos
          "MacOS"
        * 
          TRUE
          "something else"
```

## How to Contribute

Fork repository, make changes, send us a pull request.
We will review your changes and apply them to the `master` branch shortly,
provided they don't violate our quality standards. To avoid frustration,
before sending us your pull request please run full Maven build:

```bash
$ mvn clean install -Pqulice
```

You will need Maven 3.3+ and Java 8+.

