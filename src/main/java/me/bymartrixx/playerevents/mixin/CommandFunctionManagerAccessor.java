package me.bymartrixx.playerevents.mixin;

import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.server.function.FunctionLoader;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Collection;

@Mixin(CommandFunctionManager.class)
public interface CommandFunctionManagerAccessor {
    @Accessor("field_25333")
    FunctionLoader getFunctionLoader();

    @Invoker("method_29460")
    void invokeExecuteAll(Collection<CommandFunction> functions, Identifier tag);
}
