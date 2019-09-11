

# SourceClear Invoker

This project is designed to project a simple wrapper around the existing SourceClear command line in order to provide the ability to parse the results and output a configurable JUnit pass/fail test. This can then be used as part of a pipeline in order to verify source repositories and final deliverables.

It has a pre-requisite that SourceClear has been installed via its rpm (it should check for that). The yum repo for that is:

    [SourceClear]
    name=SourceClear
    baseurl=https://download.sourceclear.com/redhat/noarch/
    enabled=1
    gpgcheck=1
    gpgkey=https://download.sourceclear.com/redhat/SRCCLR-GPG-KEY

Currently this project will build a jar-with-dependencies although this is primarily aimed at local testing only. It provides a simplified interface to SourceClear e.g.

``` bash
Usage: SrcClrWrapper [-dehV] [--email-address=<emailAddress>]
                     [--email-server=<emailServer>] [-c=<product>]
                     [--processor=<processor>] [-t=<threshold>] [COMMAND]
Wrap SourceClear and invoke it.
      --email-address=<emailAddress>[,<emailAddress>...]
                         Comma separated list of email addresses to notify. Domain portion of
                         first will be used as FROM address
      --email-server=<emailServer>
                         SMTP Server to use to send notification email
  -p, --product=<product>    Product Name
  -v, --product-version=<version>   Version of the product
  --package=<subpackage> CPE Subpackage Name
  --trace                Enables trace logging from SourceClear. Disables JSON output.
  -d, --debug            Enable debug.
  -e, --exception        Throw exception on vulnerabilities found.
  -h, --help             Show this help message and exit.
  --processor=<processor>
                         Processor to use to analyse SourceClear results. Default is
                           'cvss'
  -t, --threshold=<threshold>
                         Threshold on which exception is thrown. Only used with CVSS
                           Processor
  -V           Print version information and exit.


Usage: SrcClrWrapper scm [-dehV] [--ref=REF] --url=URL [-t=<threshold>]
Scan a SCM URL
      --ref=REF     the SCM reference (e.g. git sha, tag)
      --url=URL     the SCM url
  -d, --debug       Enable debug.
  -e, --exception   Throw exception on vulnerabilities found.
  -h, --help        Show this help message and exit.
  -t, --threshold=<threshold>
                    Threshold on which exception is thrown.
  -V    Print version information and exit.
  --maven-param     Extra Maven parameters

Usage: SrcClrWrapper binary [-dehV] --name=NAME --url=URL
                            [-t=<threshold>]
Scan a remote binary
      --url=URL     the remote file url
  -d, --debug       Enable debug.
  -e, --exception   Throw exception on vulnerabilities found.
  -h, --help        Show this help message and exit.
  -t, --threshold=<threshold>
                    Threshold on which exception is thrown.
  -V     Print version information and exit.
```

Its main use is to be ran inside Jenkins as a JUnit test suite e.g.

    mvn -Dmaven.buildNumber.skip=true -Pjenkins clean test  '-DargLine=-Dsourceclear="--product-version=1.0.0 -p=koji-build-finder scm --url=https://github.com/release-engineering/koji-build-finder.git --ref=koji-build-finder-1.0.0"'
    
## Features

* It supports reading a configuration from the command or from `$HOME/.srcclr/invoker.properties`.
* It can send a notification email to a specified email address with a summary of any problems found.
* It can examine either the CVSS score returned from SourceClear or examine the CVE identifier and then query the results using the CPE (product name) against the Red Hat Security Data API ( https://access.redhat.com/labs/securitydataapi/ )
* Sample Jenkins jobs are provided in the `jenkins` directory.

### Notes

Currently the code requires the product name and version to be passed in. It will assemble a CPE from that information. While we did consider using the [CPE parser library](https://github.com/stevespringett/CPE-Parser) but due to the fact we are not currently parsing or comparing CPEs the extra library isn't required.
