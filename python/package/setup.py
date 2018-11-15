from setuptools import setup
import streamsx.messagehub
setup(
  name = 'streamsx.messagehub',
  packages = ['streamsx.messagehub'],
  include_package_data=True,
  version = streamsx.messagehub.__version__,
  description = 'IBM Streams Event Streams Hub integration',
  long_description = open('DESC.txt').read(),
  author = 'IBM Streams @ github.com',
  author_email = 'debrunne@us.ibm.com',
  license='Apache License - Version 2.0',
  url = 'https://github.com/ddebrunner/streamsx.messagehub',
  keywords = ['streams', 'ibmstreams', 'streaming', 'analytics', 'streaming-analytics', 'messaging', 'messagehub', 'events'],
  classifiers = [
    'Development Status :: 3 - Alpha',
    'License :: OSI Approved :: Apache Software License',
    'Programming Language :: Python :: 3',
    'Programming Language :: Python :: 3.5',
    'Programming Language :: Python :: 3.6',
  ],
  install_requires=['streamsx>=1.11.5a'],
  
  test_suite='nose.collector',
  tests_require=['nose']
)
