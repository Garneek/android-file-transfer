import datetime


def get_current_date():
	return datetime.datetime.now().strftime("%Y.%m.%d %H:%M")

def get_not_ready_date():
	return "9999.99.99 99:99"	