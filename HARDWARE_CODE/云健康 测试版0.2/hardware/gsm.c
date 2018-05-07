/***************************************************************
gsm模块开机发送的字符串
RDY\r\n
+CFUN: 1\r\n
+CPIN: READY\r\n
Call Ready\r\n
SMS Ready\r\n



发送短信的说明

发送英文短信，以下为串口发送的内容
AT+CMGF=1\r\n    //设置为TEXT模式
AT+CSCA="+8613800290500"\r\n    //设置短信服务中心号码
AT+CMGS="+8618829291175"\r\n    //接收者的手机号码
Location:E,108.901683,N,34.157907    //短信内容，短信内容不需要""，不要<\r\n>
0x1A    //短信最后以十六进制发送0x1A，不要<\r\n>


发送中文短信，以下为串口发送的内容
AT+CMGF=0\r\n    //设置为PDU模式
AT+CMGS=45\r\n    //45为短信总长度，指的是从11000D91开始(含11000D91)到结尾的字节数，每两个数字为一字节，它的实质是将十六进制数据以字符形式发送。
0891683108200905F011000D91688128291971F50008AA128FD9662F4E0067616D4B8BD577ED4FE13002    //短信内容，短信内容不需要""，不要<\r\n>
0x1A    //短信最后以十六进制发送0x1A，不要<\r\n>


//以下为中文短信内容格式说明
0891//固定内容
683108200905F0//含86的每两位调换的短信服务中心号，最后一位补F
11000D91//固定内容
685191418135F8//含86的每两位调换的收信人手机号，最后一位补F
0008AA//固定内容
12//十六进制的短信真实内容的长度，每两个数字为一字节，它的实质是将十六进制数据以字符形式发送。
8FD9662F4E0067616D4B8BD577ED4FE13002//短信真实内容
这  是  一  条  测  试  短  信  。  //短信内容

“11000D91 + 电话号码 + 0008AA + 短信真实内容长度”总长为15字节

短信文本采用Unicode大端方式编码！
短信文本采用Unicode大端方式编码！
短信文本采用Unicode大端方式编码！

***************************************************************/

#include "gsm.h"
#include "usart.h"
#include "delay.h"
#include "gps.h"
#include "io.h"

//const char GSM_SSCN[13] = "8613800290500";
//const char GSM_AN[13] = "8618829291175";

char ServiceCenterNumber[15] = {0};//短信服务中心号码总长14，最后一位放“\0”
char AddresseeNumber[15] = {0};//收信人号码总长14，最后一位放“\0”
char LocationMessage[34] = {0};//定位短信，短信总长33，最后一位放“\0”

const char SmsReady[9] = {'S','M','S',' ','R','e','a','d','y'};

//启动成功返回1，启动失败返回0
unsigned char GSMReady(void)
{
	unsigned char i;
	
	if(USART_RX_STA & 0x8000)
	{
		USART_RX_STA = 0;//串口接收完成后一定要手动清零！！！！！！！！
		for(i=0; i<9; i++)
		{
			if(USART_RX_BUF[i] != SmsReady[i])
			{
				return 0;
			}
		}
		
		return 1;
	}
	
	return 0;
}

//转换号码的格式
void ChangeNumber(char *RawNum, char *AimNum)
{
	unsigned char i;
	
	for(i=0; i<12; i+=2)
	{
		AimNum[i] = RawNum[i+1];
		AimNum[i+1] = RawNum[i];
	}
	AimNum[12] = 'F';
	AimNum[13] = RawNum[12];
	AimNum[14] = '\0';
}

void SendGSMCommand(char *p)
{
	unsigned short int i;
	
	i = 0;
	while(p[i] != '\0')
	{
		USART1->DR = p[i];
		while ((USART1->SR & 0x40)==0)
		{
		}
		i++;
	}
}

//这段程序没什么难度，但是它最大的特点就是“乱”！！！！！
void MakeLocationMessage(void)
{
	int tmp, i;
	char arr[9];
	
	LocationMessage[0] = 'L';
	LocationMessage[1] = 'o';
	LocationMessage[2] = 'c';
	LocationMessage[3] = 'a';
	LocationMessage[4] = 't';
	LocationMessage[5] = 'i';
	LocationMessage[6] = 'o';
	LocationMessage[7] = 'n';
	LocationMessage[8] = ':';
	
	//保存经度方向
	LocationMessage[9] = LongtitudeDirection;
	LocationMessage[10] = ',';
	
	//这个用于处理经度坐标，默认经度整数部分3位加小数部分6位一共9位，西藏那边经度地低于100，最高位补0
	tmp = (int)(ChangeCoord(longtitude) * 1000000);//注意运算优先级，先计算longtitude*1000000，再转int
	for(i=9; i>0; i--)
	{
		arr[i-1] = (tmp%10) + 48;//数值加48就是ASCII码
		tmp /= 10;
	}
	
	//保存经度
	for(i=0; i<3; i++)
	{
		LocationMessage[11+i] = arr[i];
	}
	LocationMessage[14] = '.';
	for(i=3; i<9; i++)
	{
		LocationMessage[15+i-3] = arr[i];
	}
	
	LocationMessage[21] = ',';//这里别忘了逗号
	

	//保存纬度方向
	LocationMessage[22] = LatitudeDirection;
	LocationMessage[23] = ',';
	
	//这个用于处理纬度坐标，默认纬度整数部分2位加小数部分6位一共8位，南海纬度低于10，最高位补0
	tmp = (int)(ChangeCoord(latitude) * 1000000);//注意运算优先级，先计算longtitude*1000000，再转int
	for(i=8; i>0; i--)
	{
		arr[i-1] = (tmp%10) + 48;//数值加48就是ASCII码
		tmp /= 10;
	}
	
	//保存纬度
	for(i=0; i<2; i++)
	{
		LocationMessage[24+i] = arr[i];
	}
	LocationMessage[26] = '.';
	for(i=2; i<8; i++)
	{
		LocationMessage[27+i-2] = arr[i];
	}
	
	LocationMessage[33] = '\0';//千万别忘了这个！
}


//发个短信能累死……
void SendLocationMessage(void)
{
	SendGSMCommand("AT+CMGF=1\r\n");//转换为TEXT模式，说人话就是英文模式
	delay_ms(500);
	//这三行合起来是AT+CSCA="+含86的短信服务中心号码"\r\n
	SendGSMCommand("AT+CSCA=\"+");
	SendGSMCommand(GSM_SSCN);
	SendGSMCommand("\"\r\n");
	delay_ms(500);
	//这三行合起来是AT+CSCA="+含86的收信人号码"\r\n
	SendGSMCommand("AT+CMGS=\"+");
	SendGSMCommand(GSM_AN);
	SendGSMCommand("\"\r\n");
	delay_ms(500);
	
	MakeLocationMessage();
	SendGSMCommand(LocationMessage);

	delay_ms(1000);
	delay_ms(1000);
	delay_ms(1000);
	delay_ms(1000);
	
	//短信结束符
	USART1->DR = 0x1A;
	while ((USART1->SR & 0x40)==0)
	{
	}
	
	delay_ms(1000);
	delay_ms(1000);
}

void SendFallDownMessage(void)
{
	SendGSMCommand("AT+CMGF=0\r\n");//转换为PDU模式，说人话就是中文模式
	delay_ms(500);
	SendGSMCommand("AT+CMGS=23\r\n");//短信长度
	delay_ms(500);
	
	SendGSMCommand("0891");
	SendGSMCommand(ServiceCenterNumber);
	SendGSMCommand("11000D91");
	SendGSMCommand(AddresseeNumber);
	SendGSMCommand("0008AA");
	SendGSMCommand("08");//十六进制的短信真实内容的长度
	SendGSMCommand("6211645450124E86");//发送“我摔倒了”，长度8
	
	delay_ms(1000);
	delay_ms(1000);
	delay_ms(1000);
	delay_ms(1000);
	
	USART1->DR = 0x1A;
	while ((USART1->SR & 0x40)==0)
	{
	}
	
	delay_ms(1000);
	delay_ms(1000);
}

void SendAlarmMessage(void)
{
	SendGSMCommand("AT+CMGF=0\r\n");//转换为PDU模式，说人话就是中文模式
	delay_ms(500);
	SendGSMCommand("AT+CMGS=29\r\n");//短信长度
	delay_ms(500);
	
	SendGSMCommand("0891");
	SendGSMCommand(ServiceCenterNumber);
	SendGSMCommand("11000D91");
	SendGSMCommand(AddresseeNumber);
	SendGSMCommand("0008AA");
	SendGSMCommand("0E");//十六进制的短信真实内容的长度
	SendGSMCommand("6211904752307D27602560C551B5");//发送“我遇到紧急情况”，长度14
	
	delay_ms(1000);
	delay_ms(1000);
	delay_ms(1000);
	delay_ms(1000);
	
	USART1->DR = 0x1A;
	while ((USART1->SR & 0x40)==0)
	{
	}
	
	delay_ms(1000);
	delay_ms(1000);
}

//睡眠成功返回1，否则返回0
unsigned char GSMSleep(void)
{	
	SendGSMCommand("at+csclk=1\r\n");//发送at指令进入睡眠模式
	
	while(1)
	{
		if(USART_RX_STA & 0x8000)
		{
			USART_RX_STA = 0;//串口接收完成后一定要手动清零！！！！！！！！
			
			if(USART_RX_BUF[0] == 'O' && USART_RX_BUF[1] == 'K')
			{
				return 1;
			}
		}
	}
//	return 0;
}

//唤醒成功返回1，否则返回0
unsigned char GSMWakeup(void)
{
	//使能端拉低50ms以后，gsm串口有效
	GSM_EN = 0;
	delay_ms(80);

	SendGSMCommand("at+csclk=0\r\n");//发送at指令唤醒

//	delay_ms(10);
	
	while(1)
	{
		if(USART_RX_STA & 0x8000)
		{
			USART_RX_STA = 0;//串口接收完成后一定要手动清零！！！！！！！！
			
			if(USART_RX_BUF[0] == 'O' && USART_RX_BUF[1] == 'K')
			{
				GSM_EN = 1;//使能端置为高电平
				return 1;
			}
		}
	}
//	return 0;
}
