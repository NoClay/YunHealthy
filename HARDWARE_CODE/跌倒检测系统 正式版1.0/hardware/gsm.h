#ifndef GSM_H
#define GSM_H

//Sms Service Center Number，短信服务中心号码，陕西是13800290500，必须包含86，且为字符串形式
#define GSM_SSCN "8613800290500"
//Addressee Number收信人号码，必须包含86，且为字符串形式
#define GSM_AN "8618829291175"
//#define GSM_AN "8618829289875"

//extern const char GSM_SSCN[13];
//extern const char GSM_AN[13];
//const char GSM_SSCN[13] = "8613800290500";
//const char GSM_AN[13] = "8618829291175";

extern char ServiceCenterNumber[15];//短信服务中心号码总长14，最后一位放“\0”
extern char AddresseeNumber[15];//收信人号码总长14，最后一位放“\0”
extern char LocationMessage[34];//定位短信，短信总长33，最后一位放“\0”

unsigned char GSMReady(void);
unsigned char GSMSleep(void);
unsigned char GSMWakeup(void);

void ChangeNumber(char *RawNum, char *AimNum);//传入原号码存放的数组和转换后存放号码的数组
void SendGSMCommand(char *p);//不含\r\n，发送时要注意！！！
void SendLocationMessage(void);
void SendFallDownMessage(void);
void SendAlarmMessage(void);

#endif
