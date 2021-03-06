---
title: HO release notes
layout: page
---

Changelist HO! 3.0

## Some numbers: 
  - 90 commits
  - 293 files changed (11,027 additions and 6,341 deletions)
  - 42 issues closed
  - 4 contributors


## Highlights


  - in case of promotion/demotion information about the new league will be available as soon as last game of the season has been played

  - impact of special events on score for both you and your opponent based on latest lineup information

  - new match report mocking HT full report
  
  - full control on which game to download (e.g. exclude HTO integrated games)


## Detailed Changelog

### Download

- [NEW] Full control on which game to download (e.g. exclude HTO integrated games)  #290


### Squad

  - [FIX] ordering of best position is now saved on closing #397
  - [NEW] manual adjustment of experience level (similar to other skills) #463
  - [FIX] Fix team summary comparison after new download from HT #475


### Team Analyser

 - [NEW] impact of special events on score for both you and your opponent based on latest lineup information #299
 - [FIX] fix calculation of HatStats and Loddar in TeamAnalyzer #464

### Rating

 - [NEW] impact of match orders on rating is now directly visible from the subsitution tab, helping finding the best time for subsitution change  #294
 - [FIX] all match orders are now consider in minute-bases prediction rating (behaviour change, position wap ...). Until now only substitution were considered #385
 - [FIX] counterattackrating calculation beyond divine has been fixed #398


### Matches

- [NEW] new match report mocking HT full report #421
- [FIX] Matches Overview NPE #396
- [FIX] NPE when trying to simulate upcoming games #472
- [FIX] Improved performance when first displaying the tab when the database contains lots of matches #471
- [FIX] Improved performance when exiting HO when the database is big #471
- [NEW] removed Match Analyzer module #477


### Lineup

- [FIX] to improve visibility, only last name is displayed in combo box, but the full player name is available via infobull on mouse hovering  #394
- [FIX] player name column is now resizable  #382


### Training

- [NEW] Training tab now shows information about skill devaluation (this can be disable in preference) #286
- [FIX] Training history table skill values of players with more than one skill devaluations fixed #444
- [FIX] fix an issue when new player had a skill increase on first training and before it was ever imported into HO! #188


### Misc

   - [FIX] player age calculation is now correct #422
   - [FIX] Index Out Of Bounds Exception at startup on new DBs #448
   - [FIX] fix multiple display issues about player names (composed name, nicknames)  #451
   - [FIX] removed deprecated training block feature  #486
   

### League

   - [NEW] in case of promotion/demotion information about the new league will be available as soon as last game of the season has been played #247
   - [FIX] HO! will now automatically recognize league change at the begining of the season #391
   - [FIX] Fix position arrows colour in series table #499


## Translations
  - HO! is currently available in 36 languages thanks to the work of 56 translators. The translation status varies a lot from one language to another. If you can help in a language requiring attention please join in the effort and register on [POeditor](https://poeditor.com/join/project/jCaWGL1JCl):
  - Translators contribution since the previous release (number of translated terms):


| username       | nb translated terms |
|----------------|:-------------------:|
| Lidegand       | 378                 |
| Saleh          | 65                  |
| Mauro Aranda   | 62                  |
| sich           | 58                  |
| mondstern      | 40                  |
| h3t3r0         | 9                   |
| murko          | 6                   |

