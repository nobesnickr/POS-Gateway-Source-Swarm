#  Copyright (c) 2014 Sonrisa Informatikai Kft. All Rights Reserved.
# 
#  This software is the confidential and proprietary information of
#  Sonrisa Informatikai Kft. ("Confidential Information").
#  You shall not disclose such Confidential Information and shall use it only in
#  accordance with the terms of the license agreement you entered into
#  with Sonrisa.
# 
#  SONRISA MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
#  THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
#  TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
#  PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SONRISA SHALL NOT BE LIABLE FOR
#  ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
#  DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
#
#  Python script which converts Swarm logs into SQL
#

import argparse
import re

# Parse input argument
parser = argparse.ArgumentParser(description='Convert Swarm logs to SQL')
parser.add_argument('--log', help='Input log file')
args = parser.parse_args()

# Regular expression for log lines
pattern = re.compile("^(\d\d\d\d-\d\d-\d\d\s\d\d:\d\d:\d\d),\d\d\d\s(\w*)\s*\[(\w*)\]\s-\s(.*)$")

# Start SQL content
print ("CREATE TABLE IF NOT EXISTS logs (log_id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY, time TIMESTAMP, level VARCHAR(20), source VARCHAR(255), message TEXT);")

# Read lines in file
line_number = 0
logfile = open(args.log, "r" )
for line in logfile:

	# Try to match line to the regular expression
	result = pattern.search(line)
	if result is not None:
		str = ""
		
		# If this is the first line, add header's insert
		if line_number == 0:
			str += "INSERT INTO logs (time, level, source, message) VALUES "
		# Otherwise separate value blocks
		else:
			str += ","
					
		# I know I shouldn't really be using re's escape, but MySQL's but I don't have that module 
		# on Windows. 
		str += "('" + result.group(1) + "','" +  result.group(2) + "','" + result.group(3) + "','" + re.escape(result.group(4) )+ "')"

		# Restart insert query after 128. MySQL limit is actually 1024
		if line_number == 128:
			str += ";"
			line_number = 0
		else:
		 line_number=line_number+1
		 
		print(str)	

# Close last line too
print (";")
logfile.close()