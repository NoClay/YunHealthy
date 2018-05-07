/***************************************************************
gsmģ�鿪�����͵��ַ���
RDY\r\n
+CFUN: 1\r\n
+CPIN: READY\r\n
Call Ready\r\n
SMS Ready\r\n



���Ͷ��ŵ�˵��

����Ӣ�Ķ��ţ�����Ϊ���ڷ��͵�����
AT+CMGF=1\r\n    //����ΪTEXTģʽ
AT+CSCA="+8613800290500"\r\n    //���ö��ŷ������ĺ���
AT+CMGS="+8618829291175"\r\n    //�����ߵ��ֻ�����
Location:E,108.901683,N,34.157907    //�������ݣ��������ݲ���Ҫ""����Ҫ<\r\n>
0x1A    //���������ʮ�����Ʒ���0x1A����Ҫ<\r\n>


�������Ķ��ţ�����Ϊ���ڷ��͵�����
AT+CMGF=0\r\n    //����ΪPDUģʽ
AT+CMGS=45\r\n    //45Ϊ�����ܳ��ȣ�ָ���Ǵ�11000D91��ʼ(��11000D91)����β���ֽ�����ÿ��������Ϊһ�ֽڣ�����ʵ���ǽ�ʮ�������������ַ���ʽ���͡�
0891683108200905F011000D91688128291971F50008AA128FD9662F4E0067616D4B8BD577ED4FE13002    //�������ݣ��������ݲ���Ҫ""����Ҫ<\r\n>
0x1A    //���������ʮ�����Ʒ���0x1A����Ҫ<\r\n>


//����Ϊ���Ķ������ݸ�ʽ˵��
0891//�̶�����
683108200905F0//��86��ÿ��λ�����Ķ��ŷ������ĺţ����һλ��F
11000D91//�̶�����
685191418135F8//��86��ÿ��λ�������������ֻ��ţ����һλ��F
0008AA//�̶�����
12//ʮ�����ƵĶ�����ʵ���ݵĳ��ȣ�ÿ��������Ϊһ�ֽڣ�����ʵ���ǽ�ʮ�������������ַ���ʽ���͡�
8FD9662F4E0067616D4B8BD577ED4FE13002//������ʵ����
��  ��  һ  ��  ��  ��  ��  ��  ��  //��������

��11000D91 + �绰���� + 0008AA + ������ʵ���ݳ��ȡ��ܳ�Ϊ15�ֽ�

�����ı�����Unicode��˷�ʽ���룡
�����ı�����Unicode��˷�ʽ���룡
�����ı�����Unicode��˷�ʽ���룡

***************************************************************/

#include "gsm.h"
#include "usart.h"
#include "delay.h"
#include "gps.h"
#include "io.h"

//const char GSM_SSCN[13] = "8613800290500";
//const char GSM_AN[13] = "8618829291175";

char ServiceCenterNumber[15] = {0};//���ŷ������ĺ����ܳ�14�����һλ�š�\0��
char AddresseeNumber[15] = {0};//�����˺����ܳ�14�����һλ�š�\0��
char LocationMessage[34] = {0};//��λ���ţ������ܳ�33�����һλ�š�\0��

const char SmsReady[9] = {'S','M','S',' ','R','e','a','d','y'};

//�����ɹ�����1������ʧ�ܷ���0
unsigned char GSMReady(void)
{
	unsigned char i;
	
	if(USART_RX_STA & 0x8000)
	{
		USART_RX_STA = 0;//���ڽ�����ɺ�һ��Ҫ�ֶ����㣡��������������
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

//ת������ĸ�ʽ
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

//��γ���ûʲô�Ѷȣ������������ص���ǡ��ҡ�����������
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
	
	//���澭�ȷ���
	LocationMessage[9] = LongtitudeDirection;
	LocationMessage[10] = ',';
	
	//������ڴ��������꣬Ĭ�Ͼ�����������3λ��С������6λһ��9λ�������Ǳ߾��ȵص���100�����λ��0
	tmp = (int)(ChangeCoord(longtitude) * 1000000);//ע���������ȼ����ȼ���longtitude*1000000����תint
	for(i=9; i>0; i--)
	{
		arr[i-1] = (tmp%10) + 48;//��ֵ��48����ASCII��
		tmp /= 10;
	}
	
	//���澭��
	for(i=0; i<3; i++)
	{
		LocationMessage[11+i] = arr[i];
	}
	LocationMessage[14] = '.';
	for(i=3; i<9; i++)
	{
		LocationMessage[15+i-3] = arr[i];
	}
	
	LocationMessage[21] = ',';//��������˶���
	

	//����γ�ȷ���
	LocationMessage[22] = LatitudeDirection;
	LocationMessage[23] = ',';
	
	//������ڴ���γ�����꣬Ĭ��γ����������2λ��С������6λһ��8λ���Ϻ�γ�ȵ���10�����λ��0
	tmp = (int)(ChangeCoord(latitude) * 1000000);//ע���������ȼ����ȼ���longtitude*1000000����תint
	for(i=8; i>0; i--)
	{
		arr[i-1] = (tmp%10) + 48;//��ֵ��48����ASCII��
		tmp /= 10;
	}
	
	//����γ��
	for(i=0; i<2; i++)
	{
		LocationMessage[24+i] = arr[i];
	}
	LocationMessage[26] = '.';
	for(i=2; i<8; i++)
	{
		LocationMessage[27+i-2] = arr[i];
	}
	
	LocationMessage[33] = '\0';//ǧ������������
}


//������������������
void SendLocationMessage(void)
{
	SendGSMCommand("AT+CMGF=1\r\n");//ת��ΪTEXTģʽ��˵�˻�����Ӣ��ģʽ
	delay_ms(500);
	//�����к�������AT+CSCA="+��86�Ķ��ŷ������ĺ���"\r\n
	SendGSMCommand("AT+CSCA=\"+");
	SendGSMCommand(GSM_SSCN);
	SendGSMCommand("\"\r\n");
	delay_ms(500);
	//�����к�������AT+CSCA="+��86�������˺���"\r\n
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
	
	//���Ž�����
	USART1->DR = 0x1A;
	while ((USART1->SR & 0x40)==0)
	{
	}
	
	delay_ms(1000);
	delay_ms(1000);
}

void SendFallDownMessage(void)
{
	SendGSMCommand("AT+CMGF=0\r\n");//ת��ΪPDUģʽ��˵�˻���������ģʽ
	delay_ms(500);
	SendGSMCommand("AT+CMGS=23\r\n");//���ų���
	delay_ms(500);
	
	SendGSMCommand("0891");
	SendGSMCommand(ServiceCenterNumber);
	SendGSMCommand("11000D91");
	SendGSMCommand(AddresseeNumber);
	SendGSMCommand("0008AA");
	SendGSMCommand("08");//ʮ�����ƵĶ�����ʵ���ݵĳ���
	SendGSMCommand("6211645450124E86");//���͡���ˤ���ˡ�������8
	
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
	SendGSMCommand("AT+CMGF=0\r\n");//ת��ΪPDUģʽ��˵�˻���������ģʽ
	delay_ms(500);
	SendGSMCommand("AT+CMGS=29\r\n");//���ų���
	delay_ms(500);
	
	SendGSMCommand("0891");
	SendGSMCommand(ServiceCenterNumber);
	SendGSMCommand("11000D91");
	SendGSMCommand(AddresseeNumber);
	SendGSMCommand("0008AA");
	SendGSMCommand("0E");//ʮ�����ƵĶ�����ʵ���ݵĳ���
	SendGSMCommand("6211904752307D27602560C551B5");//���͡����������������������14
	
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

//˯�߳ɹ�����1�����򷵻�0
unsigned char GSMSleep(void)
{	
	SendGSMCommand("at+csclk=1\r\n");//����atָ�����˯��ģʽ
	
	while(1)
	{
		if(USART_RX_STA & 0x8000)
		{
			USART_RX_STA = 0;//���ڽ�����ɺ�һ��Ҫ�ֶ����㣡��������������
			
			if(USART_RX_BUF[0] == 'O' && USART_RX_BUF[1] == 'K')
			{
				return 1;
			}
		}
	}
//	return 0;
}

//���ѳɹ�����1�����򷵻�0
unsigned char GSMWakeup(void)
{
	//ʹ�ܶ�����50ms�Ժ�gsm������Ч
	GSM_EN = 0;
	delay_ms(80);

	SendGSMCommand("at+csclk=0\r\n");//����atָ���

//	delay_ms(10);
	
	while(1)
	{
		if(USART_RX_STA & 0x8000)
		{
			USART_RX_STA = 0;//���ڽ�����ɺ�һ��Ҫ�ֶ����㣡��������������
			
			if(USART_RX_BUF[0] == 'O' && USART_RX_BUF[1] == 'K')
			{
				GSM_EN = 1;//ʹ�ܶ���Ϊ�ߵ�ƽ
				return 1;
			}
		}
	}
//	return 0;
}
