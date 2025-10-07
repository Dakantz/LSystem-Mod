package at.tugraz.lsysmode;

import at.tugraz.lsysmode.lsysvar.LSysVarBlockEntityRender;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import static at.tugraz.lsysmode.lsysvar.LSysVarRegistry.LSYS_VAR_BLOCK_ENTITY;

public class EventHandler {

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(
                // The block entity type to register the renderer for.
                LSYS_VAR_BLOCK_ENTITY.get(),
                // A function of BlockEntityRendererProvider.Context to BlockEntityRenderer.
                LSysVarBlockEntityRender::new
        );


    }
}
