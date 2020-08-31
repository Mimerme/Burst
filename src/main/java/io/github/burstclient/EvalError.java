/*
 * Copyright (C) 2014 - 2020 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package io.github.burstclient;

import net.wurstclient.util.SwingUtils;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class EvalError extends JDialog
{
	public static void main(String[] args)
	{
		SwingUtils.setLookAndFeel();
		JOptionPane.showMessageDialog(null,
				args[0].substring(0,Math.min(1000, args[0].length())),
				"JS Evaluation Error",
				JOptionPane.WARNING_MESSAGE);
	}
}
