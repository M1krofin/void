package content.skill.prayer.active

import content.entity.combat.hit.combatAttack
import content.skill.prayer.praying
import world.gregs.voidps.engine.entity.character.player.skill.Skill

combatAttack { player ->
    if (damage <= 40 || !player.praying("smite")) {
        return@combatAttack
    }
    target.levels.drain(Skill.Prayer, damage / 40)
}
