/*
Yaaic - Yet Another Android IRC Client

Copyright 2009-2013 Sebastian Kaspari

This file is part of Yaaic.

Yaaic is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Yaaic is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Yaaic.  If not, see <http://www.gnu.org/licenses/>.
 */
package ir.occc.android.irc.command.handler;

import ir.occc.android.R;
import ir.occc.android.irc.command.BaseHandler;
import ir.occc.android.irc.exception.CommandException;
import ir.occc.android.irc.IRCService;
import ir.occc.android.model.Conversation;
import ir.occc.android.model.Server;

import android.content.Context;

/**
 * Command: /raw <line>
 * 
 * Send a raw line to the server
 * 
 * @author Sebastian Kaspari <sebastian@yaaic.org>
 */
public class RawHandler extends BaseHandler
{
    /**
     * Execute /raw
     */
    @Override
    public void execute(String[] params, Server server, Conversation conversation, IRCService service) throws CommandException
    {
        if (params.length > 1) {
            String line = BaseHandler.mergeParams(params);
            service.getConnection(server.getId()).sendRawLineViaQueue(line);
        } else {
            throw new CommandException(service.getString(R.string.line_missing));
        }
    }

    /**
     * Usage of /raw
     */
    @Override
    public String getUsage()
    {
        return "/raw <line>";
    }

    /**
     * Description of /raw
     */
    @Override
    public String getDescription(Context context)
    {
        return context.getString(R.string.command_desc_raw);
    }
}
