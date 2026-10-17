package com.rotpaddon.exampleaddon.action;

import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.StandAction;
import com.github.standobyte.jojo.client.ClientUtil;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.rotpaddon.exampleaddon.client.gui.AlcorFormSelectionScreen;

import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

/** Tusk-style selector action: the action itself opens a menu, it does not change the form. */
public class AlcorSelectFormAction extends StandAction {
    public AlcorSelectFormAction(StandAction.Builder builder) {
        super(builder);
    }

    @Override
    protected void perform(World world, LivingEntity user,
                           IStandPower power, ActionTarget target) {
        if (world.isClientSide() && user == ClientUtil.getClientPlayer()) {
            AlcorFormSelectionScreen.open();
        }
    }
}
