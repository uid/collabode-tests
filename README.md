Collabode Tests
===============

http://uid.csail.mit.edu/collabode

Collabode is a web-based collaborative software development environment
powered by Eclipse and EtherPad.

This repository contains unit, integration, and browser automation tests.

**Collabode source: https://github.com/uid/collabode**


Generating Eclipse configuration files
--------------------------------------

    mvn eclipse:clean eclipse:eclipse replacer:replace


Running browser tests
---------------------

 * Obtain the [Selenium][] Server standalone ```.jar``` file, perhaps from
   [Selenium downloads][]
 * Run with ```java -jar selenium-server-standalone-[ver].jar``` on a machine
   with Firefox installed
 * Copy ```config/tests-browser.properties.example``` to
   ```config/tests-browser.properties``` and modify:
   * ```seleniumIP``` is the address of the Selenium server (e.g. localhost,
     another machine, VM)
   * ```listenIP``` is the address of localhost that will be used to access
     Collabode (e.g. localhost, external IP, VM subnet IP)
 * Right-click and "Run As" ```tests-browser.launch```

  [Selenium]: http://seleniumhq.org/
  [Selenium downloads]: http://seleniumhq.org/download/
