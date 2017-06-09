from django.conf.urls import url
from . import views

urlpatterns = [
    url(r'^hitters/$', views.hitter_list, name='hitter_list'),
    url(r'^pitchers/$', views.pitcher_list, name='pitcher_list'),
    url(r'^teams/$', views.team_list, name='team_list'),
    url(r'^schedules/$', views.schedule_list, name='schedule_list'),
    url(r'^predictions/$', views.prediction_list, name='prediction_list'),
    url(r'^make_database/hitter/$', views.make_database_hitter, name='make_database_hitter'),
    url(r'^make_database/pitcher/$', views.make_database_pitcher, name='make_database_pitcher'),
    url(r'^make_database/team/$', views.make_database_team, name='make_database_team'),
    url(r'^make_database/schedule/$', views.make_database_schedule, name='make_database_schedule'),
    url(r'^authenticate/$', views.authenticate, name='authenticate'),
    url(r'^prediction/$', views.prediction, name='prediction'),
    url(r'^entry/$', views.entry, name='entry'),
    url(r'^calculate/$', views.calculate, name='calculate'),
]

# url(r'^hitters/(?P<pk>[0-9]+)/$', views.hitter_detail),