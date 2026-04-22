package content.area.misthalin.lumbridge

import world.gregs.voidps.engine.Script
import world.gregs.voidps.engine.data.definition.Areas
import world.gregs.voidps.engine.entity.character.move.tele
import world.gregs.voidps.engine.entity.character.npc.NPCs
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.client.clearCamera
import world.gregs.voidps.engine.client.moveCamera
import world.gregs.voidps.engine.client.turnCamera
import world.gregs.voidps.engine.client.ui.open
import world.gregs.voidps.type.Direction
import world.gregs.voidps.type.Region
import world.gregs.voidps.type.Tile
import content.quest.quest
import content.quest.startCutscene
import content.quest.smallInstance
import content.quest.instanceOffset
import content.entity.player.dialogue.type.*
import content.entity.player.dialogue.*
import world.gregs.voidps.engine.entity.character.player.name
import content.quest.clearInstance
import world.gregs.voidps.type.Delta

//https://www.youtube.com/watch?v=iFn58j-xdvQ&t
class Xenia : Script {
    val area = Areas["lumbridge_catacombs"]
    val region = Region(15446)

    init {
        npcOperate("Talk-to", "xenia") {
            when (quest("blood_pact")) {
                "unstarted" -> unstarted()
                "started" -> started()
            }
        }

        npcOperate("Talk-to", "xenia_after_cutscene") {
            when (quest("blood_pact")) {
                "after_cutscene" -> {
                    npc<Shifty>("There's a guard in the room ahead. Together we should be able to take him out.")
                    choice {
                        whatIsThePlan()
                        whatIsBloodPact()
                        letsGoWithPlan()
                    }
                }
            }
        }

        objectOperate("Climb-down", "lumbridge_catacombs_entrance") {
            when (quest("blood_pact")) {
                //https://youtu.be/x9l0PtEnGUI?si=7Qkm3rqpW63JKq2t&t=275
                "unstarted" -> npc<Amazed>("xenia", "Hey! I want to talk to you!")
                "started" -> cutscene()
                "after_cutscene" -> enterInstance()
            }
        }

        objectOperate("Climb-up", "lumbridge_catacombs_exit") {
            exitInstance()
        }

        entered("lumbridge_catacombs") {
            if (quest("blood_pact") == "after_cutscene") {
                enterInstance()
            }
        }
    }

    suspend fun Player.unstarted() {
        npc<Quiz>("It's $name isn't it? I'm glad you've come by. I need some help.")
        choice {
            helpWith()
            whoAreYou()
            howDidYouKnow()
            sorryGotToGo()
        }
    }

    //https://youtu.be/3dR8pfKlrFA?si=IGPntQCnoqk8vbHV&t=65
    suspend fun Player.started() {
        npc<Neutral>("When you're ready, head into the catacombs. I'll follow.")
    }


    fun Player.enterInstance() {
        val offset = instanceOffset()
        tele(offset.tile(3877, 5527, 1))
        spawnInstance(offset)
    }

    fun Player.exitInstance() {
        clearInstance()
        tele(Tile(3246, 3198, 0))
    }

    fun spawnInstance(offset: Delta) {
        NPCs.add("xenia_after_cutscene", offset.tile(3877, 5526, 1), Direction.NORTH)
        NPCs.add("kayle_attackable", offset.tile(3877, 5543, 1), Direction.SOUTH)
        NPCs.add("caitlin_attackable", offset.tile(3864, 5538, 1), Direction.EAST)
        NPCs.add("ilona_tied", offset.tile(3865, 5523, 2), Direction.NORTH)
        NPCs.add("reese_attackable", offset.tile(3865, 5523, 2), Direction.SOUTH)
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
        //npc<Laugh>("Who doesn't know who you are? You're a legend even in the Legend's Guild! It's an honour to meet you, $name.")
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

    fun ChoiceOption.tellMeMoreCultists(): Unit = option<Neutral>("Tell me more about these cultists.") {
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

    fun ChoiceOption.enoughQuestions(): Unit = option<Neutral>("Enough questions.") {
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
    fun ChoiceOption.sorryGotToGo(): Unit = option<Neutral>("Sorry, I've got to go")

    //https://youtu.be/Q5jTvPUN0EA?si=IMBUVKpZ5rr__mz9&t=77
    fun ChoiceOption.whatIsThePlan(): Unit = option<Quiz>("What's the plan of attack?") {
        npc<Neutral>("It looks like the cultist has a sling. The best way to deal with someone with a ranged weapon is to get close to them and attack with melee.")
        choice {
            whatIsBloodPact()
            letsGoWithPlan()
        }
    }

    fun ChoiceOption.whatIsBloodPact(): Unit = option<Quiz>("What's a blood pact") {
        npc<Neutral>("It's something Zamorakian cults do sometimes; a way of swearing loyalty to their leader.")
        npc<Neutral>("A blood pact doesn't have real magical power, but that kind of thing can have a great power over a person if they believe strongly enough.")
        choice {
            whatIsThePlan()
            letsGoWithPlan()
        }
    }

    fun ChoiceOption.letsGoWithPlan(): Unit = option<Neutral>("Let's get on with this.")

    suspend fun Player.cutscene() {
        open("fade_out")
        val instance = smallInstance(region)
        val offset = instanceOffset()
        val cutscene = startCutscene("cultists_inside", instance, offset)
        delay(4)
        tele(offset.tile(3878, 5548, 1), clearInterfaces = false)
        face(Direction.SOUTH)
        val ilona = NPCs.add("ilona_cutscene", offset.tile(3877, 5532, 1), Direction.NORTH)
        val kayle = NPCs.add("kayle_cutscene", offset.tile(3876, 5531, 1), Direction.NORTH)
        val caitlin = NPCs.add("caitlin_cutscene", offset.tile(3878, 5531, 1), Direction.NORTH)
        val reese = NPCs.add("reese_cutscene", offset.tile(3877, 5531, 1), Direction.NORTH)
        moveCamera(offset.tile(3876, 5546, 1), 340)
        turnCamera(offset.tile(3877, 5541, 1), 150)
        open("fade_in")
        kayle.walkTo(offset.tile(3876, 5535, 1))
        caitlin.walkTo(offset.tile(3878, 5540, 1))
        ilona.walkTo(offset.tile(3877, 5541, 1))
        delay(1)
        reese.walkTo(offset.tile(3877, 5540, 1))
        delay(10)
        reese.face(kayle)
        npc<Frustrated>("reese_cutscene", "Come on, Kayle! We don't have forever.")
        kayle.walkTo(offset.tile(3876, 5539, 1))
        npc<Teary>("kayle_cutscene", "Look, Reese; are you sure about this? There must be some other way we can...")
        npc<Frustrated>("reese_cutscene", "We made a blood pact, Kayle! The three of us are in this all the way.")
        npc<Teary>("kayle_cutscene", "Yes, but...")
        caitlin.face(reese)
        npc<Frustrated>("caitlin_cutscene", "Do we have to take this idiot?")
        reese.face(caitlin)
        npc<Angry>("reese_cutscene", "Yes! The blood pact! You read the book!")
        npc<Scared>("ilona_cutscene", "Let me go! I didn't make any blood pact with-")
        reese.face(ilona)
        npc<Angry>("reese_cutscene", "Shut up!")
        npc<Frustrated>("reese_cutscene", "Kayle, you stay here. Guard the door.")
        npc<Frustrated>("reese_cutscene", "You, come on.")
        ilona.walkTo(offset.tile(3877, 5543, 1))
        caitlin.walkTo(offset.tile(3878, 5542, 1))
        reese.walkTo(offset.tile(3877, 5542, 1))
        delay(1)
        open("fade_out")
        delay(4)
        NPCs.remove(ilona)
        NPCs.remove(kayle)
        NPCs.remove(caitlin)
        NPCs.remove(reese)
        cutscene.end(destroyInstance = false)
        set("blood_pact", "after_cutscene")
        tele(offset.tile(3877, 5527, 1))
        clearCamera()
        clearAnim()
        spawnInstance(offset)
        open("fade_in")
        delay(1)
        npc<Neutral>("xenia_after_cutscene", "Looks like there's a guard ahead. We should take him together.")
    }
}