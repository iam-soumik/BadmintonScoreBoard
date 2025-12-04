package com.scoreboard.badminton;

import com.scoreboard.badminton.Model.Game;
import com.scoreboard.badminton.Model.Match;
import com.scoreboard.badminton.Model.Team;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by HOME on 12/29/2018.
 */

public class BSBGlobalVariable {
    public static Match currentMatch;
    public static Game currentGame;
    public static int gameNumber = 0;
    public static LinkedHashMap<Team, String> scoreBoardTeamMap; // This map initialize first time only. It is used to display the team name in the scoreboard.
    public static List<Team> nextMatchTeam;
}
