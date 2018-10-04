

# SourceClear Invoker

This project is designed to project a simple wrapper around the existing SourceClear command line in order to provide the ability to parse the results and output a configurable JUnit pass/fail test. This can then be used as part of a pipeline in order to verify source repositories and final deliverables.

It has a pre-requisisite that SourceClear has been installed via its rpm (it should check for that). The yum repo for that is:

    [SourceClear]
    name=SourceClear
    baseurl=https://download.sourceclear.com/redhat/noarch/
    enabled=1
    gpgcheck=1
    gpgkey=https://download.sourceclear.com/redhat/SRCCLR-GPG-KEY

Currently this project will build a jar-with-dependencies although this is primarily aimed at local testing only. It provides a simplified interface to SourceClear e.g.

``` bash
Usage: SrcClrWrapper scm [-dehV] [--ref=REF] --url=URL [-t=<threshold>]
Scan a SCM URL
      --ref=REF     the SCM reference (e.g. git sha, tag)
      --url=URL     the SCM url
  -d, --debug       Enable debug.
  -e, --exception   Throw exception on vulnerabilities found.
  -h, --help        Show this help message and exit.
  -t, --threshold=<threshold>
                    Threshold on which exception is thrown.
  -V, --version     Print version information and exit.


Usage: SrcClrWrapper binary [-dehV] --name=NAME --rev=REV --url=URL
                            [-t=<threshold>]
Scan a remote binary
      --name=NAME   Name of binary
      --rev=REV     Version of the binary
      --url=URL     the remote file url
  -d, --debug       Enable debug.
  -e, --exception   Throw exception on vulnerabilities found.
  -h, --help        Show this help message and exit.
  -t, --threshold=<threshold>
                    Threshold on which exception is thrown.
  -V, --version     Print version information and exit.
```

Its main use is to be ran inside Jenkins as a JUnit test suite e.g.

    mvn -Pjenkins clean test -DargLine='-Dsourceclear="--url=https://github.com/release-engineering/koji-build-finder.git --ref=koji-build-finder-1.0.0"'

Note that a Jenkins job is provided in the `jenkins/SC.yml` directory.
