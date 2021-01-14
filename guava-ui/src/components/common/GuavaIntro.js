/*global kakao*/
import React, {useEffect} from 'react'
import {useRecoilState} from 'recoil';
import classNames from 'classnames/bind';
import ReactGA from 'react-ga';

import styles from './guavaIntro.module.scss';
import {Button, Modal, WhiteSpace, WingBlank} from 'antd-mobile';
import GuavaIntroHeader from '../header/GuavaIntroHeader';
import {currentRegionState} from '../datatool/state';

const cx = classNames.bind(styles);

const GuavaIntro = () => {
    const [currentRegion, setCurrentRegion] = useRecoilState(currentRegionState);

    useEffect(() => {
        ReactGA.initialize('UA-129316162-1');
    }, []);

    return (
        <>
            <GuavaIntroHeader/>
            {/*<WingBlank>*/}
            {/*    <WhiteSpace/>*/}
            {/*    <Button onClick={() => Modal.prompt(currentRegion ? currentRegion.name : '', '수정이 필요한 내용을 입력하세요', [*/}
            {/*        {text: '닫기'},*/}
            {/*        {*/}
            {/*            text: '전송', onPress: value => {*/}
            {/*                ReactGA.ga('send', 'event', 'issues', (currentRegion ? currentRegion.name : '-'), value);*/}
            {/*            }*/}
            {/*        },*/}
            {/*    ], 'default', '')}>이슈 제보하기</Button>*/}
            {/*    <WhiteSpace/>*/}
            {/*</WingBlank>*/}
        </>
    )
};


export default GuavaIntro;