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
package ir.occc.android.irc.exception;

/**
 * The CommandException is thrown on command execution if the
 * command couldn't be executed due to invalid params
 * 
 * @author Sebastian Kaspari
 */
public class CommandException extends Throwable
{
    private static final long serialVersionUID = -8317993941455253288L;

    /**
     * Create a new CommandException object
     */
    public CommandException(String message)
    {
        super(message);
    }
}
