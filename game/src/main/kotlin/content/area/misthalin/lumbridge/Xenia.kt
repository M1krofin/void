package content.area.misthalin.lumbridge

import world.gregs.voidps.engine.Script
import world.gregs.voidps.engine.entity.character.player.name
import content.entity.player.dialogue.type.*
import content.entity.player.dialogue.Quiz
import content.entity.player.dialogue.Sad
import content.entity.player.dialogue.Frustrated
import content.entity.player.dialogue.Happy
import content.entity.player.dialogue.Neutral
import content.entity.player.dialogue.Chuckle
import content.entity.player.dialogue.Shifty
import content.entity.player.dialogue.Pleased
import content.entity.player.dialogue.Talk
import content.entity.player.dialogue.Amazed
import content.quest.quest
import content.quest.startCutscene
import world.gregs.voidps.engine.client.clearCamera
import world.gregs.voidps.engine.client.moveCamera
import world.gregs.voidps.engine.client.turnCamera
import world.gregs.voidps.engine.client.ui.dialogue.talkWith
import world.gregs.voidps.engine.client.ui.open
import world.gregs.voidps.engine.entity.character.move.tele
import world.gregs.voidps.engine.entity.character.npc.NPCs
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.obj.GameObjects
import world.gregs.voidps.engine.inject
import world.gregs.voidps.type.Direction
import world.gregs.voidps.type.Region
import content.entity.player.dialogue.Angry
import content.entity.player.dialogue.Afraid
import content.entity.player.dialogue.Teary
import content.quest.Cutscene
import world.gregs.voidps.type.Tile
import world.gregs.voidps.engine.client.variable.hasClock
import world.gregs.voidps.engine.client.variable.start

//https://www.youtube.com/watch?v=iFn58j-xdvQ&t
class Xenia : Script {

    val region = Region(15446)
    val objects: GameObjects by inject()
    val npcs: NPCs by inject()

    init {
        npcOperate("Talk-to", "xenia") {
            when (quest("blood_pact")) {
                "unstarted" -> {
                    npc<Quiz>("It's $name isn't it? I'm glad you've come by. I need some help.")
                    choice {
                        helpWith()
                        whoAreYou()
                        howDidYouKnow()
                        sorryGotToGo()
                    }
                }
                //https://youtu.be/3dR8pfKlrFA?si=IGPntQCnoqk8vbHV&t=65
                "started" -> {
                    npc<Talk>("When you're ready, head down into the catacombs, and I'll follow.")
                }
            }

            // After quest started inside catacomb
            // after cutscene

            //
            // after if talked
            // npc<Shifty>("There's a guard in the room ahead. Together we should be able to take him out.")

        }
        npcOperate("Talk-to", "xenia_after_cutscene") {
            when (quest("blood_pact")) {
                "after_cutscene" -> {
                    choice {
                        option("What's the plan of attack?") {

                        }
                        option("What's a blood pact") {

                        }

                        option("Let's get on with this.") {

                        }
                    }
                }
            }
        }
        //https://youtu.be/x9l0PtEnGUI?si=7Qkm3rqpW63JKq2t&t=275
        objectOperate("Climb-down", "lumbridge_catacombs_entrance") {
            when (quest("blood_pact")) {
                "unstarted" -> npc<Amazed>("xenia", "Hey! I want to talk to you!")
                "started" -> cutscene()
                "after_cutscene" -> enterInstance()
            }
        }

        moved {
            if (exitArea(this, tile)) {
                val cutscene: Cutscene = remove("blood_pact_cutscene") ?: return@moved
                Script.launch { cutscene.end() }
            }
        }

        entered("lumbridge_catacombs") {
            if (quest("blood_pact") == "after_cutscene" && !hasClock("blood_pact_instance_exit")) {
                Script.launch { cutscene() }
            }
        }
        playerSpawn {
            val instanceRegion = 15446
            if (tile.region.id == instanceRegion) {
                tele(Tile(3246, 3198, 0))
            }
        }
        playerDespawn {
            cleanupInstance()
        }
    }

    fun ChoiceOption.helpWith(): Unit = option<Quiz>("What do you need help with?") {
        npc<Frustrated>("Some cultists of Zamorak have gone into the catacombs with a prisoner. I don't know what they're planning, but I'm pretty sure it's not a tea party.")
        npc<Sad>("There are three of them, and I'm not as young as I was the last time I was here. I don't want to go down there without backup.")
        choice {
            iWillHelpYou()
            iNeedToKnow()
            whoAreYou()
            howDidYouKnow()
            sorryGotToGo()
        }
    }

    fun ChoiceOption.whoAreYou(): Unit = option<Quiz>("Who are you?") {
        npc<Neutral>("My name's Xenia. I'm an adventurer.")
        npc<Neutral>("I'm one of the old guard, I suppose. I helped found the Champion's Guild, and I've done a fair few quests in my time.")
        npc<Neutral>("Now I'm starting to get a bit old for action, which is why I need your help.")
        choice {
            helpWith()
            howDidYouKnow()
            sorryGotToGo()
        }
    }

    fun ChoiceOption.howDidYouKnow(): Unit = option<Quiz>("How did you know who I am?") {
        //https://youtu.be/-hre53SZ9kA?si=-HU7LwB8LZ2TxJao&t=568
        npc<Shifty>("Oh, I have my ways. I get the feeling that you're one to watch; you could be quite the hero some day.")
        //todo when player has 300 quest points (If you are eligible to receive the Helmet of Trials)
        //npc<Chuckle>("Who doesn't know who you are? You're a legend even in the Legend's Guild! It's an honour to meet you, $name.")
        choice {
            helpWith()
            whoAreYou()
            sorryGotToGo()
        }
    }

    fun ChoiceOption.iWillHelpYou(): Unit = option<Neutral>("I'll help you.") {
        set("blood_pact", "started")
        npc<Happy>("I knew you would!")
        npc<Neutral>("We've got no time to lose. You head down the stairs, and I'll follow.")
    }

    //https://www.youtube.com/watch?v=lnBiBvkFdcQ
    fun ChoiceOption.iNeedToKnow(): Unit = option<Neutral>("I need to know more before I help you.") {
        npc<Pleased>("Very wise. I got into a lot of trouble in my youth by rushing in without knowing a situation.")
        choice {
            tellMeMoreCultists()
            whoDidTheyKidnap()
            whatDownThere()
            isThereReward()
            enoughQuestions()
        }
    }

    fun ChoiceOption.tellMeMoreCultists(): Unit = option<Talk>("Tell me more about these cultists.") {
        npc<Neutral>("Lumbridge is a Saradominist town, but there will always be some people drawn to worship Zamorak. They must have found some ritual that they think will give them power over other people.")
        choice {
            whoDidTheyKidnap()
            whatDownThere()
            isThereReward()
            enoughQuestions()
        }
    }

    fun ChoiceOption.whoDidTheyKidnap(): Unit = option<Quiz>("Who did they kidnap?") {
        npc<Sad>("A young woman named Ilona. She had just left Lumbridge to start training at the Wizards' Tower")
        npc<Frustrated>("They just grabbed her on the road. If she'd been a full wizard then she'd have been able to fight them off, but without training she didn't have a chance.")
        choice {
            tellMeMoreCultists()
            whatDownThere()
            isThereReward()
            enoughQuestions()
        }
    }

    fun ChoiceOption.whatDownThere(): Unit = option<Quiz>("What's down there?") {
        npc<Sad>("The catacombs of Lumbridge Church. The dead of Lumbridge have been buried there since...well, for about forty years now.")
        choice {
            tellMeMoreCultists()
            whoDidTheyKidnap()
            isThereReward()
            enoughQuestions()
        }
    }

    fun ChoiceOption.isThereReward(): Unit = option<Quiz>("Is there a reward if I help you?") {
        npc<Neutral>("I can't offer anything myself, but I know the cultists all have weapons, and you'll be able to keep them if we succeed. This adventure would also help to train your combat skills.")
        choice {
            tellMeMoreCultists()
            whoDidTheyKidnap()
            whatDownThere()
            enoughQuestions()
        }
    }

    fun ChoiceOption.enoughQuestions(): Unit = option<Talk>("Enough questions.") {
        npc<Quiz>("So, will you help me, $name?")
        choice {
            iWillHelpYou()
            iNeedToKnow()
            whoAreYou()
            howDidYouKnow()
            sorryGotToGo()
        }
    }

    //https://www.youtube.com/watch?v=cOmx9cf4NLM
    fun ChoiceOption.sorryGotToGo(): Unit = option("Sorry, I've got to go") {
        player<Neutral>("Sorry, I've got to go")
    }

    suspend fun Player.cutscene() {
        open("fade_out")
        val cutscene = startCutscene("cultists_inside", region)
        set("blood_pact_cutscene", cutscene)
        delay(4)
        tele(cutscene.tile(3878, 5548, 1), clearInterfaces = false)
        face(Direction.SOUTH)
        val ilona = npcs.add("ilona_cutscene", cutscene.tile(3877, 5532, 1), Direction.NORTH)
        val kayle = npcs.add("kayle_cutscene", cutscene.tile(3876, 5531, 1), Direction.NORTH)
        val caitlin = npcs.add("caitlin_cutscene", cutscene.tile(3878, 5531, 1), Direction.NORTH)
        val reese = npcs.add("reese_cutscene", cutscene.tile(3877, 5531, 1), Direction.NORTH)
        moveCamera(cutscene.tile(3876, 5546, 1), 340)
        turnCamera(cutscene.tile(3877, 5541, 1), 150)
        open("fade_in")
        kayle.walkTo(cutscene.tile(3876, 5535, 1))
        caitlin.walkTo(cutscene.tile(3878, 5540, 1))
        ilona.walkTo(cutscene.tile(3877, 5541, 1))
        delay(1)
        reese.walkTo(cutscene.tile(3877, 5540, 1))
        delay(10)
        reese.face(kayle)
        npc<Frustrated>("reese_cutscene", "Come on, Kayle! We don't have forever.")
        kayle.walkTo(cutscene.tile(3876, 5539, 1))
        npc<Teary>("kayle_cutscene", "Look, Reese; are you sure about this? There must be some other way we can...")
        reese.face(kayle)
        npc<Frustrated>("reese_cutscene", "We made a blood pact, Kayle! The three of us are in this all the way.")
        npc<Teary>("kayle_cutscene", "Yes, but...")
        caitlin.face(reese)
        npc<Frustrated>("caitlin_cutscene", "Do we have to take this idiot?")
        reese.face(caitlin)
        npc<Angry>("reese_cutscene", "Yes! The blood pact! You read the book!")
        npc<Afraid>("ilona_cutscene", "Let me go! I didn't make any blood pact with-")
        reese.face(ilona)
        npc<Angry>("reese_cutscene", "Shut up!")
        reese.face(kayle)
        npc<Frustrated>("reese_cutscene", "Kayle, you stay here. Guard the door.")
        reese.face(ilona)
        npc<Frustrated>("reese_cutscene", "You, come on.")
        ilona.walkTo(cutscene.tile(3877, 5543, 1))
        caitlin.walkTo(cutscene.tile(3878, 5542, 1))
        reese.walkTo(cutscene.tile(3877, 5542, 1))
        delay(1)
        open("fade_out")
        delay(4)
        cutscene.onEnd {
            Script.launch {
                start("blood_pact_instance_exit", 2)
                tele(cutscene.convert(Tile(3877, 5527, 1)))
                afterCutscene(cutscene)
                clearCamera()
                clearAnim()
                set("blood_pact", "after_cutscene")
            }
        }
    }

    suspend fun Player.afterCutscene(cutscene: Cutscene) {
        fun spawn(id: String, x: Int, y: Int, z: Int, dir: Direction) =
            npcs.add(id, cutscene.convert(Tile(x, y, z)), dir)
        spawn("xenia_after_cutscene", 3877, 5526, 1, Direction.NORTH)
        spawn("kayle_attackable", 3877, 5543, 1, Direction.SOUTH)
        spawn("caitlin_attackable", 3864, 5538, 1, Direction.EAST)
        spawn("ilona_tied", 3865, 5523, 1, Direction.NORTH)
        spawn("reese_attackable", 3865, 5523, 1, Direction.SOUTH)
    }

    fun exitArea(player: Player, to: Tile): Boolean {
        val cutscene: Cutscene = player["blood_pact_cutscene"] ?: return false
        val realTile = cutscene.original(to)
        return realTile.region.id != 15446 && !player.hasClock("blood_pact_instance_exit")
    }

    fun Player.enterInstance() {
        val cutscene: Cutscene? = this["blood_pact_cutscene"]
        if (cutscene != null) {
            tele(cutscene.convert(Tile(3877, 5527, 1)))
        } else {
            Script.launch {
                val newCutscene = startCutscene("cultists_inside", region)
                set("blood_pact_cutscene", newCutscene)
                tele(newCutscene.convert(Tile(3877, 5527, 1)))
                afterCutscene(newCutscene)
            }
        }
    }
    private fun Player.cleanupInstance() {
        val cutscene: Cutscene? = remove("blood_pact_cutscene")
        if (cutscene != null) {
            tele(Tile(3246, 3198, 0))
            cutscene.destroy()
        }
        if (tile.region.id == 15446) {
            tele(Tile(3246, 3198, 0))
        }
    }
}