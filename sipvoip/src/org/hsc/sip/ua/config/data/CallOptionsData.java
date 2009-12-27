/*
 * Copyright (C) 2008 Hughes Systique Corporation, USA (http://www.hsc.com)
 * 
 * This file is part of Sipdroid, sample SIP UI on Android
 * 
 * Sipdroid is a free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * Sipdroid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Sipdroid; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Author(s):
 * Nitin Khanna, for Hughes Systique Corporation
 */

package org.hsc.sip.ua.config.data;

public class CallOptionsData {
	private boolean m_AutoAnswer;

	public class AutoIgnore {
		private boolean m_AutoIgnore;
		private short m_AfterDuration;

		public AutoIgnore() {
			setAutoIgnore(false);
			setAfterDuration((short) 0);
		}

		public boolean isAutoIgnore() {
			return m_AutoIgnore;
		}

		public void setAutoIgnore(boolean autoIgnore) {
			m_AutoIgnore = autoIgnore;
		}

		public short getAfterDuration() {
			return m_AfterDuration;
		}

		public void setAfterDuration(short afterDuration) {
			m_AfterDuration = afterDuration;
		}
	};

	private AutoIgnore m_AutoIgnore;

	private boolean m_DoNotDisturbMode;

	private boolean m_BlockCallerId;

	public class NATRefreshParams {
		public final static byte DUMMY_PACKET = 1;
		public final static byte OPTIONS_PACKET = 2;

		private boolean m_UseNATRefresh;
		private byte m_NATRefreshType;
		private short m_NATTimeOut;

		public NATRefreshParams() {
			m_UseNATRefresh = false;
			m_NATRefreshType = 1;
			m_NATTimeOut = 0;
		}

		public boolean isUseNATRefresh() {
			return m_UseNATRefresh;
		}

		public void setUseNATRefresh(boolean useNATRefresh) {
			m_UseNATRefresh = useNATRefresh;
		}

		public byte getNATRefreshType() {
			return m_NATRefreshType;
		}

		public void setM_NATRefreshType(byte refreshType) {
			m_NATRefreshType = refreshType;
		}

		public short getNATTimeOut() {
			return m_NATTimeOut;
		}

		public void setNATTimeOut(short timeOut) {
			m_NATTimeOut = timeOut;
		}
	};

	private NATRefreshParams m_NATRefreshParams;

	public boolean isAutoAnswer() {
		return m_AutoAnswer;
	}

	public void setAutoAnswer(boolean autoAnswer) {
		m_AutoAnswer = autoAnswer;
	}

	public boolean isDoNotDisturbMode() {
		return m_DoNotDisturbMode;
	}

	public void setDoNotDisturbMode(boolean doNotDisturbMode) {
		m_DoNotDisturbMode = doNotDisturbMode;
	}

	public boolean isBlockCallerId() {
		return m_BlockCallerId;
	}

	public void setBlockCallerId(boolean blockCallerId) {
		m_BlockCallerId = blockCallerId;
	}

	public AutoIgnore getAutoIgnore() {
		return m_AutoIgnore;
	}

	public NATRefreshParams getNATRefreshParams() {
		return m_NATRefreshParams;
	}

	public CallOptionsData() {

		m_AutoIgnore = new AutoIgnore();
		m_DoNotDisturbMode = false;
		m_BlockCallerId = false;
		m_NATRefreshParams = new NATRefreshParams();
		m_AutoAnswer = false;
	}

	public void InitializeDefaultConfig() {
		m_AutoIgnore.setAutoIgnore(false);
		m_AutoIgnore.setAfterDuration((short) 15);
		m_DoNotDisturbMode = false;
		m_BlockCallerId = false;
		m_NATRefreshParams.setUseNATRefresh(false);
		m_NATRefreshParams.setNATTimeOut((short) 0);
		m_NATRefreshParams.setM_NATRefreshType(NATRefreshParams.OPTIONS_PACKET);
		m_AutoAnswer = false;
	}

	public void VerifyConfig() {
		if (m_AutoIgnore.isAutoIgnore()) {
			if (m_AutoIgnore.getAfterDuration() <= 0) {
				m_AutoIgnore.setAfterDuration((short) 15);
			}
		}
		if (m_NATRefreshParams.isUseNATRefresh()) {
			if (m_NATRefreshParams.getNATRefreshType() != NATRefreshParams.OPTIONS_PACKET
					&& m_NATRefreshParams.getNATRefreshType() != NATRefreshParams.DUMMY_PACKET) {
				m_NATRefreshParams
						.setM_NATRefreshType(NATRefreshParams.OPTIONS_PACKET);
			}

			if (m_NATRefreshParams.getNATTimeOut() <= 0) {
				m_NATRefreshParams.setNATTimeOut((short) 15);
			}
		}
	}

}
