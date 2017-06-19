from django.http import HttpResponse, JsonResponse
from django.views import View
from django.views.decorators.csrf import csrf_exempt
from rest_framework.renderers import JSONRenderer
from rest_framework.parsers import JSONParser
from crawling.models import Hitter, Pitcher, Team, Schedule, User, Prediction, Gift, EntryList
from crawling.serializers import HitterSerializer, PitcherSerializer, TeamSerializer, ScheduleSerializer, PredictionSerializer
from urllib.request import urlopen
from bs4 import BeautifulSoup
from datetime import datetime
import requests
import json
import re


class JSONResponse(HttpResponse):
    """
    콘텐츠를 JSON으로 변환한 후 HttpResponse 형태로 반환합니다.
    """
    def __init__(self, data, **kwargs):
        content = JSONRenderer().render(data)
        kwargs['content_type'] = 'application/json; charset=utf-8'
        super(JSONResponse, self).__init__(content, **kwargs)


@csrf_exempt
def authenticate(request):
    if request.method == "GET":
        username = request.GET.get("username")
        try:
            user = User.objects.get(username=username)
        except:
            user = User(username=username)
            user.save()

        ticket = user.ticket
        return HttpResponse(ticket)
    else:
        return HttpResponse("fail")


@csrf_exempt
def prediction(request):
    if request.method == "GET":
        username = request.GET.get("username")
        game_id = request.GET.get("game_id")
        result = request.GET.get("result")

        try:
            user = User.objects.get(username=username)
            game = Schedule.objects.get(game_id=game_id)
        except:
            return HttpResponse("fail1")

        if game.state == 1:
            return HttpResponse("fail2")

        try:
            pred = Prediction.objects.get(user=user, schedule=game)
        except:
            pred = Prediction(user=user, schedule=game)

        pred.result = result
        pred.save()

        return HttpResponse("success")
    return HttpResponse("fail3")


@csrf_exempt
def entry(request):
    if request.method == "GET":
        username = request.GET.get("username")
        pk = request.GET.get("number")

        try:
            user = User.objects.get(username=username)
            gift = Gift.objects.get(pk=pk)
        except:
            return HttpResponse("fail1")

        if user.ticket <= 0:
            return HttpResponse("fail2")

        entry = EntryList(user=user, gift=gift)
        entry.count += 1
        user.ticket -= 1
        user.save()
        entry.save()
        return HttpResponse("success")
    return HttpResponse(request.method)


@csrf_exempt
def hitter_list(request):
    """
    코드 조각을 모두 보여주거나 새 코드 조각을 만듭니다.
    """
    if request.method == 'GET':
        hitters = Hitter.objects.all()
        serializer = HitterSerializer(hitters, many=True)
        return JSONResponse(serializer.data)

    elif request.method == 'POST':
        data = JSONParser().parse(request)
        serializer = HitterSerializer(data=data)
        if serializer.is_valid():
            serializer.save()
            return JSONResponse(serializer.data, status=201)
        return JSONResponse(serializer.errors, status=400)


@csrf_exempt
def pitcher_list(request):
    """
    코드 조각을 모두 보여주거나 새 코드 조각을 만듭니다.
    """
    if request.method == 'GET':
        pitchers = Pitcher.objects.all()
        serializer = PitcherSerializer(pitchers, many=True)
        return JSONResponse(serializer.data)

    elif request.method == 'POST':
        data = JSONParser().parse(request)
        serializer = PitcherSerializer(data=data)
        if serializer.is_valid():
            serializer.save()
            return JSONResponse(serializer.data, status=201)
        return JSONResponse(serializer.errors, status=400)


@csrf_exempt
def team_list(request):
    """
    코드 조각을 모두 보여주거나 새 코드 조각을 만듭니다.
    """
    if request.method == 'GET':
        teams = Team.objects.all()
        serializer = TeamSerializer(teams, many=True)
        return JSONResponse(serializer.data)

    elif request.method == 'POST':
        data = JSONParser().parse(request)
        serializer = TeamSerializer(data=data)
        if serializer.is_valid():
            serializer.save()
            return JSONResponse(serializer.data, status=201)
        return JSONResponse(serializer.errors, status=400)


@csrf_exempt
def schedule_list(request):
    """
    코드 조각을 모두 보여주거나 새 코드 조각을 만듭니다.
    """
    if request.method == 'GET':
        schedules = Schedule.objects.all()
        serializer = ScheduleSerializer(schedules, many=True)
        return JSONResponse(serializer.data)

    elif request.method == 'POST':
        data = JSONParser().parse(request)
        serializer = ScheduleSerializer(data=data)
        if serializer.is_valid():
            serializer.save()
            return JSONResponse(serializer.data, status=201)
        return JSONResponse(serializer.errors, status=400)


@csrf_exempt
def prediction_list(request):
    """
    코드 조각을 모두 보여주거나 새 코드 조각을 만듭니다.
    """
    if request.method == 'GET':
        user = User.objects.get(username=request.GET.get("username"))
        predictions = Prediction.objects.filter(user=user)
        serializer = PredictionSerializer(predictions, many=True)
        return JSONResponse(serializer.data)

    elif request.method == 'POST':
        data = JSONParser().parse(request)
        serializer = PredictionSerializer(data=data)
        if serializer.is_valid():
            serializer.save()
            return JSONResponse(serializer.data, status=201)
        return JSONResponse(serializer.errors, status=400)


def make_database_hitter(request):
    data = urlopen('http://www.koreabaseball.com/Record/Player/HitterBasic/Basic1.aspx')
    soup = BeautifulSoup(data.read(), "html.parser")

    i = 0
    hitters = []
    for tr in soup.find_all('tr'):
        if i < 1:
            i += 1
        else:
            row = 0
            player = {}
            for td in tr.find_all('td'):
                if row == 0:
                    player['rank'] = td.text
                elif row == 1:
                    player['name'] = td.find('a').text
                elif row == 2:
                    player['team'] = td.text
                else:
                    player[td.get('data-id')] = td.text

                row += 1
            hitters.append(player)

    for hitter in hitters:
        try:
            player = Hitter.objects.get(name=hitter['name'])
        except:
            player = Hitter()

        player.name = hitter['name']
        player.rate = hitter['HRA_RT']
        player.homerun = hitter['HR_CN']
        player.point = hitter['RBI_CN']
        player.team = hitter['team']
        player.save()

    return HttpResponse("OK")


def make_database_pitcher(request):
    data = urlopen('http://www.koreabaseball.com/Record/Player/PitcherBasic/Basic1.aspx')
    soup = BeautifulSoup(data.read(), "html.parser")

    i = 0
    hitters = []
    for tr in soup.find_all('tr'):
        if i < 1:
            i += 1
        else:
            row = 0
            player = {}
            for td in tr.find_all('td'):
                if row == 0:
                    player['rank'] = td.text
                elif row == 1:
                    player['name'] = td.find('a').text
                elif row == 2:
                    player['team'] = td.text
                else:
                    player[td.get('data-id')] = td.text

                row += 1
            hitters.append(player)

    for pitcher in hitters:
        try:
            player = Pitcher.objects.get(name=pitcher['name'])
        except:
            player = Pitcher()

        player.name = pitcher['name']
        player.era = pitcher['ERA_RT']
        player.so = pitcher['KK_CN']
        player.win = pitcher['W_CN']
        player.team = pitcher['team']
        player.save()

    return HttpResponse("OK")


def make_database_team(request):
    data = urlopen('http://www.koreabaseball.com/TeamRank/TeamRank.aspx')
    soup = BeautifulSoup(data.read(), "html.parser")

    i = 0
    teams = []
    for tr in soup.find_all('tr'):
        if i < 1:
            i += 1
        else:
            row = 0
            team = {}
            for td in tr.find_all('td'):
                if row == 1:
                    team['name'] = td.text
                elif row == 2:
                    team['win'] = td.text
                elif row == 3:
                    team['lose'] = td.text
                elif row == 4:
                    team['draw'] = td.text
                elif row == 5:
                    team['rate'] = td.text
                elif row == 6:
                    team['diff'] = td.text

                row += 1
            i += 1
            teams.append(team)

        if i == 11:
            break

    for team in teams:
        try:
            t = Team.objects.get(name=team['name'])
        except:
            t = Team()

        t.name = team['name']
        t.win = team['win']
        t.lose = team['lose']
        t.draw = team['draw']
        t.diff = team['diff']
        t.rate = team['rate']
        t.save()

    return HttpResponse("OK")


def make_database_schedule(request):
    ss = Schedule.objects.all()
    ss.delete()

    url = 'http://www.koreabaseball.com/ws/Schedule.asmx/GetScheduleList'
    params = {
        "leId": 1,
        "srIdList": '0,9',
        "seasonId": '2017',
        "gameMonth": '06',
        "teamId": "",
    }
    response = requests.post(url, data=params)
    js = json.loads(response.text)

    cycle = 0
    schedules = []
    int_parser = re.compile('\d+')
    day = ""
    for rows in js["rows"]:
        index = 0
        schedule = {}

        for row in rows["row"]:
            info = row["Text"]
            if cycle == 0:
                if index == 0:
                    info = info[0:5]
                    schedule['day'] = info
                    day = info
                elif index == 1:
                    info = info.replace('<b>', '')
                    info = info.replace('</b>', '')
                    schedule['time'] = info

                elif index == 2:
                    info = info.replace('<em>', "")
                    info = info.replace('</em>', "")
                    info = info.replace('vs', " ")
                    info = info.replace('<span class="lose">', "")
                    info = info.replace('<span class="win">', "")
                    info = info.replace('<span class="same">', "")
                    info = info.replace('</span>', "")
                    info = info.replace('<span>', "")
                    if len(info) > 6:
                        schedule['away_score'] = int_parser.findall(info)[0]
                        schedule['home_score'] = int_parser.findall(info)[1]
                        schedule['state'] = 1
                        for k in range(0, 10):
                            info = info.replace(str(k), "")
                    else:
                        schedule['state'] = 0

                    schedule['away_team'] = info.split(" ")[0]
                    schedule['home_team'] = info.split(" ")[1]

                elif index == 7:
                    schedule['stadium'] = info
            else:
                if index == 0:
                    info = info.replace('<b>', '')
                    info = info.replace('</b>', '')
                    schedule['time'] = info
                    schedule['day'] = day
                elif index == 1:
                    info = info.replace('<em>', "")
                    info = info.replace('</em>', "")
                    info = info.replace('vs', " ")
                    info = info.replace('<span class="lose">', "")
                    info = info.replace('<span class="win">', "")
                    info = info.replace('<span class="same">', "")
                    info = info.replace('</span>', "")
                    info = info.replace('<span>', "")
                    if len(info) > 6:
                        schedule['away_score'] = int_parser.findall(info)[0]
                        schedule['home_score'] = int_parser.findall(info)[1]
                        schedule['state'] = 1
                        for k in range(0, 10):
                            info = info.replace(str(k), "")

                    else:
                        schedule['state'] = 0

                    schedule['away_team'] = info.split(" ")[0]
                    schedule['home_team'] = info.split(" ")[1]

                elif index == 6:
                    schedule['stadium'] = info

            index += 1

        schedules.append(schedule)
        cycle = (cycle + 1) % 5

    idx = 0
    for schedule in schedules:
        s = Schedule()
        s.game_id = idx
        # s.home_pitcher = schedule["home_pitcher"]
        s.home_team = schedule["home_team"]
        try:
            s.home_score = schedule["home_score"]
            s.away_score = schedule["away_score"]
        except:
            pass
        # s.away_pitcher = schedule["away_pitcher"]
        s.away_team = schedule["away_team"]
        s.stadium = schedule["stadium"]
        s.time = schedule["time"]
        s.state = schedule["state"]

        s.day = schedule['day']
        s.save()
        idx += 1

    return HttpResponse("OK")


def calculate(request):
    preds = Prediction.objects.filter(state=False)
    for pred in preds:
        if pred.schedule.state == 0:
            continue

        away = pred.schedule.away_score
        home = pred.schedule.home_score

        res = away - home
        check2 = 0
        if res > 0:
            check2 = -1
        elif res == 0:
            check2 = 0
        else:
            check2 = 1

        if pred.result == check2:
            user = pred.user
            user.ticket += 1
            pred.check = "success"
            user.save()
            pred.save()

    return HttpResponse("OK")