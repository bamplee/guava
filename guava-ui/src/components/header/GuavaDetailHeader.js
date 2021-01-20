import React, {useEffect} from 'react'

import {useHistory, useParams} from 'react-router-dom';

import classNames from 'classnames/bind';

import styles from './guavaHeader.module.scss';
import {useRecoilState} from 'recoil';
import {buildingState, regionState,} from '../datatool/state';
import ArrowLeftOutlined from '@ant-design/icons/es/icons/ArrowLeftOutlined';
import SearchOutlined from '@ant-design/icons/es/icons/SearchOutlined';
import {Button, Tabs, WingBlank} from 'antd-mobile';
import {getBuilding, getDetail, getRegion} from '../datatool/api';

const cx = classNames.bind(styles);

const GuavaDetailHeader = ({tabId}) => {
    const history = useHistory();
    // const [region, setRegion] = useRecoilState(regionState);
    const {regionType, buildingId, regionId} = useParams();
    const [region, setRegion] = useRecoilState(regionState);
    const [building, setBuilding] = useRecoilState(buildingState);
    // const [tab, setTab] = useState(tabId);

    useEffect(() => {
        if (regionType === 'b') {
            const init = async () => {
                setRegion(await getBuilding(regionId));
                // setBuilding(await getDetail(regionId));
            };
            init();
        } else {
            const init = async () => {
                setRegion(await getRegion(regionId));
                // setBuilding(null);
            };
            init();
        }
    }, [regionId]);

    return (
        <>
            <div className={cx('empty_container')}>
            </div>
            <div className={cx('header_container')}>
                <div className={cx('title_container')}>
                    <WingBlank>
                        <div className={cx('left')} onClick={() => history.push('/')}>
                            <ArrowLeftOutlined/>
                        </div>
                    </WingBlank>
                    <div className={cx('logo')} onClick={() => history.push('/search')}>
                        <span
                            className={cx('title')}>{region?.type === 'BUILDING' ? region?.name : region?.address}</span>
                    </div>
                    <WingBlank>
                        <div className={cx('right')}>
                            {/*<SendOutlined style={{marginRight: 15}} onClick={() => Modal.prompt(region ? region.name : '', '수정이 필요한 내용을 입력하세요', [*/}
                            {/*    {text: '닫기'},*/}
                            {/*    {*/}
                            {/*        text: '전송', onPress: value => {*/}
                            {/*            ReactGA.ga('send', 'event', 'issues', (region ? region.name : '-'), value);*/}
                            {/*        }*/}
                            {/*    },*/}
                            {/*], 'default', '')}/>*/}
                            <SearchOutlined onClick={() => history.push('/search')}/>
                        </div>
                    </WingBlank>
                </div>
                <div className={cx('filter_container')}>
                    <div className={cx('filter_select')}>
                        <Button
                            className={cx('filter_btn', tabId === 'info' && 'filter_btn_active')}
                            type={'primary'}
                            onClick={() => {
                                history.replace('/' + regionType + '/' + regionId + '/info')
                            }}
                            size={'small'}>정보</Button>
                    </div>
                    <div className={cx('filter_select')}>
                        <Button
                            className={cx('filter_btn', (tabId * 1 <= 2) && 'filter_btn_active')}
                            type={'primary'}
                            onClick={() => {
                                history.replace('/' + regionType + '/' + regionId + '/0')
                            }}
                            size={'small'}>시세</Button>
                    </div>
                    {/*<div className={cx('filter_select')}>*/}
                    {/*    <Button className={cx('filter_btn', tabId === '1' && 'filter_btn_active')}*/}
                    {/*            type={'primary'} size={'small'}*/}
                    {/*            onClick={() => {*/}
                    {/*                history.replace('/' + regionType + '/' + regionId + '/1')*/}
                    {/*            }}*/}
                    {/*    >호가</Button>*/}
                    {/*</div>*/}
                    {/*<div className={cx('filter_select')}>*/}
                    {/*    <Button className={cx('filter_btn')}*/}
                    {/*            type={'primary'} size={'small'}*/}
                    {/*            inline*/}
                    {/*            onClick={() => alert('비교')}>*/}
                    {/*        <span>비교</span>*/}
                    {/*    </Button>*/}
                    {/*</div>*/}
                </div>
            </div>
            {
                tabId !== 'info' &&
                <div style={{borderBottom: '1px solid #f2f2f2'}}>
                    <Tabs onChange={(e) => {
                        history.replace('/' + regionType + '/' + regionId + '/' + e.value)
                    }} tabs={[{title: '실거래가', value: 0}, {title: '호가', value: 1}, {title: '비교', value: 2}]}
                          page={tabId * 1}
                          animated={false}
                          useOnPan={false}/>
                </div>
            }
        </>
    )
};

export default GuavaDetailHeader;