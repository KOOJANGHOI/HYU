# -*- coding: utf-8 -*-
# Generated by Django 1.11.1 on 2017-06-06 05:43
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('crawling', '0001_initial'),
    ]

    operations = [
        migrations.AlterField(
            model_name='schedule',
            name='day',
            field=models.CharField(blank=True, max_length=110, null=True),
        ),
    ]
