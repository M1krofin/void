package content.area.misthalin.varrock.palace

import content.entity.sound.sound
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.obj.objectOperate
import world.gregs.voidps.engine.entity.obj.replace

objectOperate("Open", "varrock_manhole") {
    target.replace("varrock_manhole_open")
    player.message("You pull back the cover from over the manhole.")
    player.sound("coffin_open")
}

objectOperate("Close", "varrock_manhole_open") {
    target.replace("varrock_manhole")
    player.message("You place the cover back over the manhole.")
}
