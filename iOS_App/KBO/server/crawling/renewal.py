#!/usr/bin/env python3.6
import urllib.request

ip = 'http://172.16.172.87:8000/make_database/'
functions = ['hitter', 'pitcher', 'team']
for function in functions:
    url = '{}{}'.format(ip, function)
    res = urllib.request.urlopen(url)

