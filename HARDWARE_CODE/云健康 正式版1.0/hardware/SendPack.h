#ifndef SENDPACK_H
#define SENDPACK_H

#define DATAHEAD1 0xDC//���ڷ������ݵ�֡ͷ1
#define DATAHEAD2 0xBA//���ڷ������ݵ�֡ͷ2

void Com1SendArray(unsigned char *ArrayName, unsigned char DataType, unsigned short int length, unsigned char IfCheck);
void Com2SendArray(unsigned char *ArrayName, unsigned char DataType, unsigned short int length, unsigned char IfCheck);
void Com3SendArray(unsigned char *ArrayName, unsigned char DataType, unsigned short int length, unsigned char IfCheck);

#endif
