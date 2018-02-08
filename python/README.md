Python streamsx.messagehub package.

Note this is still a work in progress ...

This exposes SPL operators in the `com.ibm.streamsx.messagehub` toolkit as Python methods.
It is also intended as a template for exposing other toolkits as a template.

Package is organized using standard packaging to upload to PyPi.

The package is uploaded to PyPi in the standard way:
```
cd python/package
python setup.py sdist bdist_wheel upload -r pypi`
```
Note: This is done using the `ibmstreams` account at pypi.org

Package details: https://pypi.python.org/pypi/streamsx.messagehub

Documentation is using Sphinx and can be built locally using:
```
cd python/package/docs
make html
```
and viewed using
```
firefox python/package/docs/build/html/index.html
```

The documentation is also setup at `readthedocs.io` under the account: `IBMStreams`

Documentation links:
* http://streamsxmessagehub.readthedocs.io/en/pypackage

